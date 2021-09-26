package com.xixi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SportApplicationTests {

  @Autowired
  private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
    System.out.printf(passwordEncoder.encode("123456"));
	}

}
