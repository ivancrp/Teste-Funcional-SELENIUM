package br.gov.ce.tce.tasks.funcional;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class TasksTest {

//	public WebDriver acessarAplicacao() throws MalformedURLException {
//
//				
//		DesiredCapabilities cap = DesiredCapabilities.chrome();
//
//		WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
//	
//		driver.navigate()
//				.to("https://www.tce.ce.gov.br:8082/doeconsulta/paginas/consulta-textual-ou-data-edicao-do.xhtml");
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//			
//
//		return driver;
//	}

	@Test
	public void testDoeConsuta() throws MalformedURLException {

		ChromeOptions options = new ChromeOptions();
		// options.addArguments("--headless");
		HashMap<String, Object> prefs = new HashMap<>();
		prefs.put("plugins.always_open_pdf_externally", true);
		prefs.put("download.default_directory", "C:\\Users\\ivan.alves\\Downloads\\DOEPROD");
		options.setExperimentalOption("prefs", prefs);

		ChromeDriver driver = new ChromeDriver(options);
		ChromeOptions options2 = new ChromeOptions();
		// options2.addArguments("--headless");
		HashMap<String, Object> prefs1 = new HashMap<>();
		prefs1.put("plugins.always_open_pdf_externally", true);
		prefs1.put("download.default_directory", "C:\\Users\\ivan.alves\\Downloads\\DOEMIGRADO");
		options2.setExperimentalOption("prefs", prefs1);

		ChromeDriver driver2 = new ChromeDriver(options2);

		// WebDriver driver = acessarAplicacao();

		for (String data : Util.getDatasDoe()) {

			try {
				System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
				// driver.manage().window().maximize();
				driver.get(
						"https://www.tce.ce.gov.br:8082/doeconsulta/paginas/consulta-textual-ou-data-edicao-do.xhtml");

				Thread.sleep(1000);
				List<WebElement> inputs = driver.findElements(By.id("formConsultaEdicoes:dataEdicao1InputDate"));
				for (WebElement input : inputs) {
					((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('readonly','readonly')",
							input);
				}
				driver.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(Keys.chord(Keys.CONTROL, "a"),data);
				//driver.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(Keys.BACK_SPACE);
				//driver.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(data);
				System.out.println("PROD:" + data);
				driver.findElement(By.id("formConsultaEdicoes:exibir1")).click();

				/*----------------------------------------------------------------------------------------------------------------------------------------------------*/
				
			
				System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
				driver2.get("http://svtcedevap3:8080/doeconsulta/paginas/consulta-textual-ou-data-edicao-do.xhtml");
				List<WebElement> inputs2 = driver2.findElements(By.id("formConsultaEdicoes:dataEdicao1InputDate"));
				for (WebElement input2 : inputs2) {
					((JavascriptExecutor) driver2).executeScript("arguments[0].removeAttribute('readonly','readonly')",
							input2);
					Thread.sleep(1000);
				}
				driver2.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(Keys.chord(Keys.CONTROL, "a"),data);
				//driver2.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(Keys.BACK_SPACE);
				//driver2.findElement(By.id("formConsultaEdicoes:dataEdicao1InputDate")).sendKeys(data);
				System.out.println("MIGRADO:" + data);
				driver2.findElement(By.id("formConsultaEdicoes:exibir1")).click();
				Thread.sleep(1000);

				File arquivo1 = getArquivo("C:\\Users\\ivan.alves\\Downloads\\DOEMIGRADO");
				File arquivo2 = getArquivo("C:\\Users\\ivan.alves\\Downloads\\DOEPROD");

				MessageDigest md5Digest = MessageDigest.getInstance("MD5");

				String checksum2 = getFileChecksum(md5Digest, arquivo2);
				String checksum1 = getFileChecksum(md5Digest, arquivo1);

				assertEquals(checksum1, checksum2);

				while (arquivo1.delete() == false) {
					arquivo1.delete();
				}
				while (arquivo2.delete() == false) {
					arquivo2.delete();
				}

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		driver.close();
		driver2.close();
		driver.quit();
		driver2.quit();
	}

	private File getArquivo(String path) {
		File pasta = new File(path);
		while (pasta.listFiles().length == 0) {
			if (pasta.listFiles().length != 0) {
				break;
			}
		}
		for (File file : pasta.listFiles()) {
			return file;
		}
		return null;
	}

	private String getFileChecksum(MessageDigest digest, File file) throws IOException {
		// Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		// Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		// Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		;

		// close the stream; We don't need it now.
		fis.close();

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}

}
