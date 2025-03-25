package com.rober.bank.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rober.bank.dto.EmailDetails;
import com.rober.bank.entity.Transaction;
import com.rober.bank.entity.User;
import com.rober.bank.repository.TransactionRepository;
import com.rober.bank.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private TransactionRepository repository;

    private static final String FILE = "C:\\Users\\rober\\Downloads\\Report.pdf";

    private UserRepository userRepository;

    private EmailService service;

    /**
     * Obtiene la lista de transacciones dentro de un rango de fechas para un número de cuenta específico.
     * Genera un archivo PDF con el detalle de las transacciones obtenidas.
     * Envía el archivo PDF generado a través de correo electrónico.
     */

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // Filtramos las transacciones basadas en el número de cuenta y las fechas
        List<Transaction> transactionList = repository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start))
                .filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

        // Obtener la información del usuario (cliente) relacionada con la cuenta
        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        // Configuramos las propiedades del documento PDF (tamaño, fuente, etc.)
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Crear tabla con la información del banco (nombre y dirección)
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Sistema de Banco con java"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.ORANGE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("123 Main Street, Springfield"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        // Crear tabla con la información de la declaración
        PdfPTable statemenInfo = new PdfPTable(2);
        PdfPCell custumerInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        custumerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopDate.setBorder(0);

        PdfPCell name = new PdfPCell(new Phrase("CustomerName: " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        address.setBorder(0);

        // Crear tabla de transacciones (con columnas para fecha, tipo, monto y estado)
        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Date"));
        date.setBackgroundColor(BaseColor.ORANGE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.ORANGE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUT"));
        transactionAmount.setBackgroundColor(BaseColor.ORANGE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.ORANGE);
        status.setBorder(0);

        // Agregar las cabeceras de las transacciones
        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        // Agregar las transacciones a la tabla (recorremos la lista de transacciones)
        transactionList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getStatus()));
        });

        // Agregar todas las tablas al documento
        statemenInfo.addCell(custumerInfo);
        statemenInfo.addCell(statement);
        statemenInfo.addCell(endDate);
        statemenInfo.addCell(name);
        statemenInfo.addCell(space);
        statemenInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statemenInfo);
        document.add(transactionsTable);

        document.close();

        // Enviamos el reporte al correo electronico
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("kindly find your requested account statement attached!")
                .attachment(FILE)
                .build();

        service.sendEmailWithAttachment(emailDetails);

        // Retornamos la lista de transacciones procesadas
        return transactionList;
    }
}
