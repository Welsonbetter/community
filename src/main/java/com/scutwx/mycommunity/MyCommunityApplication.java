package com.scutwx.mycommunity;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.scutwx.mycommunity.mapper")
public class MyCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCommunityApplication.class, args);
	}

}
