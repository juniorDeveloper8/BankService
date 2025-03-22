package com.rober.bank.service.impl;

import com.rober.bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);

}
