package com.atguigu.gmall.service.impl;

import java.util.Arrays;
import java.util.List;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public List<UserAddress> getUserAddressList(String userId) {
		System.out.println("UserServiceImpl.....old...");
		// TODO Auto-generated method stub
		UserAddress address1 = new UserAddress(1, "北京", "1", "李老师", "010-56253825", "Y");
		UserAddress address2 = new UserAddress(2, "深圳市", "1", "王老师", "010-56253825", "N");
		/*try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return Arrays.asList(address1,address2);
	}

}
