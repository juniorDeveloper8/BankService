package com.rober.bank.service.impl;

import com.rober.bank.dto.BankResponse;
import com.rober.bank.dto.EnquiryRequest;
import com.rober.bank.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

}
