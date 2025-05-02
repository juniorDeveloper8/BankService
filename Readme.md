# 🏦 API de Servicios Bancarios

API REST desarrollada en **Spring Boot** que permite realizar operaciones bancarias como **transferencias**, **créditos**, **débitos**, autenticación de usuarios y envío de **reportes por correo electrónico**.

### Click en la imagen para ver el de funcionamiento de la api:

[![Demo en video](./doc/img.png)](https://drive.google.com/file/d/1aikjWKb9iufXhl6BYv1LNbLispGPI21h/view?usp=sharing)


## 🚀 Características

- Registro y autenticación de usuarios (JWT)
- Operaciones bancarias:
  - Transferencias entre cuentas
  - Crédito a cuenta
  - Débito desde cuenta
- Envío de reportes por correo
- Manejo de errores y validaciones
- Arquitectura RESTful

## 📦 Tecnologías

- Java 17+
- Spring Boot 3
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- MySQL
- JavaMailSender
- Swagger *(opcional, no todos los endpoints son compatibles)*

## ⚙️ Instalación

```bash
# Clona el repositorio
git clone https://github.com/juniorDeveloper8/BankService.git

# Abre con tu editor de preferencia
# Configura tu base de datos en application.properties
# Ejecuta la aplicación
./mvnw spring-boot:run
