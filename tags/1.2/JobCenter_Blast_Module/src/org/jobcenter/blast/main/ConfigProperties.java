package org.jobcenter.blast.main;

public class ConfigProperties {

	public String blast_dir;
	public String tmp_dir;
	public String db_dir;
	public String smtp_host;
	public String ip_host;
	
	public ConfigProperties(String blast, String tmp, String db, String smtp, String ip){
		blast_dir = blast;
		tmp_dir = tmp;
		db_dir = db;
		smtp_host = smtp;
		ip_host = ip;
	}
}
