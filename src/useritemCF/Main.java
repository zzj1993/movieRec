package useritemCF;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Recommend re = new Recommend();
//		re.getItemBaseRating();
		re.getUserBaseRating();
	}

}