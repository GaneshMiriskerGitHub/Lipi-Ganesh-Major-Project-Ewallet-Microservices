package com.ewallet;

import com.ewallet.service.WalletService;
import com.ewallet.service.resource.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

	@Autowired
	WalletService walletService;

	@Override
	public void run(String... args) throws Exception {

	}
}
