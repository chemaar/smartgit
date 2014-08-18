package es.inf.uc3m.kr.smartgit;

import es.inf.uc3m.kr.smartgit.utils.ApplicationContextLocator;

public class Test {

	public static void main(String []args){
		QueryLoaderDAO queryDAO = (QueryLoaderDAO) 
				ApplicationContextLocator.getApplicationContext().getBean(QueryLoaderDAO.class.getSimpleName());
		System.out.println(queryDAO.getQueries().getProperty("getPersonById"));
	}
}
