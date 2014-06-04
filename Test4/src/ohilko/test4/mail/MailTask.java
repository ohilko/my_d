package ohilko.test4.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import ohilko.test4.ListRequestActivity;
import ohilko.test4.db.Crypto;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.db.ParserXmlFile;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

public class MailTask extends AsyncTask<String, Integer, Integer> {

	private String login;
	private String password;
	private String smtp;
	private String pop3;
	private String subject;
	private Context context;
	private DatabaseConnector db;
	private boolean isSendEmail = false;
	private boolean isGetEmail = false;
	private ProgressDialog statusDialog;

	public MailTask(String login, String password, String smtp, String pop3,
			String subject, Context context, DatabaseConnector db,
			boolean isSendEmail, boolean isGetEmail) {
		this.login = login;
		this.password = password;
		this.smtp = smtp;
		this.pop3 = pop3;
		this.context = context;
		this.db = db;
		this.isSendEmail = isSendEmail;
		this.isGetEmail = isGetEmail;
		this.subject = subject;
	}

	protected void onProgressUpdate() {
	}

	protected void onPreExecute() {
		statusDialog = new ProgressDialog(context);
		if (isSendEmail) {
			statusDialog.setMessage("Отправляет...");
		}
		if (isGetEmail) {
			statusDialog.setMessage("Получает...");
		} 
	    statusDialog.setIndeterminate(false);
	    statusDialog.setCancelable(false);
	    statusDialog.show();
	}

	protected void onPostExecute(Integer result) {
		switch (result) {
		case 1: {
			db.open();
			String[] data = new String[] { login,
					Crypto.encryptionPassword(password), smtp, pop3, subject };

			Cursor mail_cursor = db.getAllRows(DatabaseConnector.TABLE_NAME[5],
					DatabaseConnector.MAIL_DATA_FIELDS, null, null);

			if (mail_cursor.moveToFirst()) {
				db.updateRow(mail_cursor.getLong(0),
						DatabaseConnector.TABLE_NAME[5],
						DatabaseConnector.MAIL_DATA_FIELDS, data);
			} else {
				db.insertRow(DatabaseConnector.TABLE_NAME[5],
						DatabaseConnector.MAIL_DATA_FIELDS, data);
			}
			db.close();
			Intent intent = new Intent(context, ListRequestActivity.class);
			context.startActivity(intent);
		}
			break;
		case 2: {
			Toast error = Toast.makeText(context, "Неверно указаны данные",
					Toast.LENGTH_LONG);
			error.show();
		}
			break;
		case 3: {

		}
			break;
		}
	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
			if (isGetEmail) {
				getEmail();
			}
			if (isSendEmail) {
				sendEmail();
			}
			return 1;
		} catch (MessagingException e) {
			return 2;
		} catch (IOException e) {
			return 3;
		}
	}

	private void sendEmail() throws MessagingException {
		String finalString = "";
		final Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "smtp");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", smtp);
		properties.setProperty("mail.smtp.user", login);
		properties.setProperty("mail.smtp.password", password);

		Session session = Session.getInstance(properties, new Authenticator() {

			private PasswordAuthentication passwordAuth = new PasswordAuthentication(
					properties.getProperty("mail.pop3.user"), properties
							.getProperty("mail.pop3.password"));

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return passwordAuth;
			}
		});

		DataHandler handler = new DataHandler(new ByteArrayDataSource(
				finalString.getBytes(), "text/plain"));
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(login));
		message.setDataHandler(handler);

		Multipart multiPart = new MimeMultipart();
		InternetAddress toAddress;
		toAddress = new InternetAddress(login);

		message.addRecipient(Message.RecipientType.TO, toAddress);
		message.setSubject(subject);
		message.setContent(multiPart);

		File file = new File("/sdcard/tmp/requests.xml");
		ParserXmlFile parser = new ParserXmlFile(file, context, db);
		int result = parser.fillXmlFile();

		MimeBodyPart attachment = new MimeBodyPart();
		FileDataSource fds = new FileDataSource("/sdcard/tmp/requests.xml");
		attachment.setDataHandler(new DataHandler(fds));
		attachment.setFileName(fds.getName());

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(attachment);

		message.setContent(mp);
		message.setSentDate(new Date());

		Transport transport = session.getTransport("smtp");
		transport.connect(smtp, login, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

	}

	private void getEmail() throws MessagingException, IOException {

		final Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "pop3");
		properties.setProperty("mail.pop3.host", pop3);
		properties.setProperty("mail.pop3.user", login);
		properties.setProperty("mail.pop3.password", password);

		Session session = Session.getInstance(properties, new Authenticator() {

			private PasswordAuthentication passwordAuth = new PasswordAuthentication(
					properties.getProperty("mail.pop3.user"), properties
							.getProperty("mail.pop3.password"));

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return passwordAuth;
			}
		});

		Store store = session.getStore("pop3");
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		Message[] message = folder.getMessages();

		for (Message mes : message) {
			if (mes.getSubject().equals(subject)) {

				final DatabaseConnector db = new DatabaseConnector(context);

				Multipart multipart = (Multipart) mes.getContent();

				for (int i = 0; i < multipart.getCount(); i++) {
					BodyPart bodyPart = multipart.getBodyPart(i);
					if (bodyPart.getFileName() != null) {
						File f = new File("/sdcard/tmp/"
								+ bodyPart.getFileName());
						FileWriter fw = new FileWriter(f);

						InputStream is = bodyPart.getInputStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(is));

						while (br.ready()) {
							fw.append((char) br.read());
							fw.flush();
						}
						fw.close();
						br.close();

						ParserXmlFile parser = new ParserXmlFile(f, context, db);
						parser.parser();
						parser.addInTableProductChild();
					}
				}
				break;
			}
		}
		folder.close(true);
		store.close();
	}
}
