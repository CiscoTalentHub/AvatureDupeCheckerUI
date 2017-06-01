package com.cisco.avature;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.custom.StyledText;

import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

//import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.poi.EncryptedDocumentException;
//import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
//import org.apache.poi.common.usermodel.Hyperlink;
//import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFFont;
//import org.apache.poi.xssf.usermodel.XSSFHyperlink;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.MessageBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DupeChecker {

	protected Shell shlAvatureUI;
	// Text Display
	private Text txtInfo;
	private WebDriver avaturePage;
	private static String outMedusa;
	private Workbook medusaWorkbook;
	private static boolean stopButton = false;
	private static boolean loginSuccess = false;
	//private static boolean loginCancel = false;
	// private Sheet dataFile;
	private static List<UserInfo> userList = new ArrayList<UserInfo>();
	private static List<AvatureInfo> avatureList = new ArrayList<AvatureInfo>();

	private static final String[] FILTER_EXTS = { "*.xlsx" };
	private static final String[] FILTER_NAMES = { "Medusa File (*.xlsx)" };
	
	//Username and Password preference file
	//Preferences preferences = 
		      //Preferences.userNodeForPackage(DupeChecker.class);
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DupeChecker window = new DupeChecker();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlAvatureUI.open();
		shlAvatureUI.layout();
		
		//Check for settings.txt file existence.  Prompt user to add username and password otherwise
		File settingsFile = new File("src/settings.txt");
		boolean exists = settingsFile.exists();
		if(!exists){
			
			MessageBox messageBox = new MessageBox(shlAvatureUI, SWT.OK | SWT.ICON_ERROR);
			messageBox.setText("Alert");
			String errorMsg = "Please Set Avature Username and Password";
			messageBox.setMessage(errorMsg);
			messageBox.open();
			
			//Create initial Settings File
			createSettingsFile();

		}
		
		while (!shlAvatureUI.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlAvatureUI = new Shell();
		shlAvatureUI.setSize(450, 594);
		shlAvatureUI.setText("Avature Dupe Checker");

		Button btnRunDupeCheck = new Button(shlAvatureUI, SWT.NONE);
		btnRunDupeCheck.setEnabled(false);
		btnRunDupeCheck.setBounds(10, 412, 414, 55);
		btnRunDupeCheck.setText("Run Dupe Check");

		txtInfo = new Text(shlAvatureUI, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtInfo.setBounds(10, 10, 414, 335);

		Button btnOpenFile = new Button(shlAvatureUI, SWT.NONE);
		btnOpenFile.setText("Open File");
		btnOpenFile.setBounds(10, 351, 414, 55);
		
		Button btnStop = new Button(shlAvatureUI, SWT.NONE);
		btnStop.setText("Stop Dupe Check");
		btnStop.setEnabled(false);
		btnStop.setBounds(10, 473, 414, 55);
		
		Menu menu = new Menu(shlAvatureUI, SWT.BAR);
		shlAvatureUI.setMenuBar(menu);
		
		MenuItem menuSettings = new MenuItem(menu, SWT.CASCADE);
		menuSettings.setText("Settings");
		
		Menu menu_1 = new Menu(menuSettings);
		menuSettings.setMenu(menu_1);
		
		MenuItem subAuthenticate = new MenuItem(menu_1, SWT.NONE);
		subAuthenticate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Open Authenticate window to set username/password for Avature Login
				Authenticate loginWindow = new Authenticate(shlAvatureUI, 3);
				loginWindow.open();
				//JSONParser parser = new JSONParser();

				if(!loginWindow.isCancelButton()){
				new Thread(new Runnable() {
					public void run() {
						try {
							
							
							CryptoPass encryptPass = new CryptoPass();
							
							Settings settings = new Settings();
							
							settings.setUsernamePass(loginWindow.getUsername(), encryptPass.encrypt(loginWindow.getPassword()));
					
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				
				} else {
					//Reset loginWindow Cancel button back to false
					loginWindow.setCancelButton(false);
					btnRunDupeCheck.setEnabled(true);
				}
				
			}
		});
		subAuthenticate.setText("Set/Reset Authentication");
		
		MenuItem subSaveDir = new MenuItem(menu_1, SWT.NONE);
		subSaveDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				 DirectoryDialog dlg = new DirectoryDialog(shlAvatureUI, SWT.OPEN);
			        
			        String dn = dlg.open();
			        if (dn != null) {
			        	//dn = dn.replace("\\", "/");
			     
			        	new Thread(new Runnable() {
							public void run() {
								try {
									
									
							        	Settings settings = new Settings();
										
										settings.setDirectory(dn.replace("\\", "/"));
										
										System.out.println("Directory is: " + settings.getDirectory());
							     
									
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
			        	
			        }
			}
		});
		subSaveDir.setText("Set Save To Directory");
		
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopButton = true;
				btnOpenFile.setEnabled(true);
				btnStop.setEnabled(false);
				String info = "User Pressed Stop Button";
				txtInfo.append("\n" + info);
			}
		});

		btnOpenFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				

				// Select Medusa File to be processed
				FileDialog dlg = new FileDialog(shlAvatureUI, SWT.OPEN);
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				// Full path of Medusa file
				String fn = dlg.open();
				/**
				// Medusa File name
				String medusaName = fn.substring(fn.lastIndexOf('\\') + 1);
				// Working Dir of Medusa File
				String dir = fn.substring(0, fn.lastIndexOf('\\') + 1);
				// Output File name and dir
				outMedusa = dir + "Checked " + medusaName;
				*/

				if (fn != null) {
					// Medusa File name
					String medusaName = fn.substring(fn.lastIndexOf('\\') + 1);
					// Working Dir of Medusa File
					//String dir = fn.substring(0, fn.lastIndexOf('\\') + 1);
					// Output File name and dir
					//Settings settings;
					try {
						Settings settings = new Settings();
						String dir = settings.getDirectory().replace('/', '\\') + "\\";
						
						outMedusa = dir + "Checked " + medusaName;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					//String dir = settings.getDirectory();
					
					//outMedusa = dir + "Checked " + medusaName;

					new Thread(new Runnable() {
						public void run() {
							try {
								// Make copy of Medusa File and rename with
								// "Checked " appended to the name
								File source = new File(fn);
								File dest = new File(outMedusa);
								Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
								// Get the Datasheet from excel file for
								// processing
								// dataFile = getDataSheet(outMedusa);
								medusaWorkbook = getDataSheet(outMedusa, txtInfo);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();

					System.out.println(fn);
					System.out.println(outMedusa);
					txtInfo.append(fn);
					// Enable btnRunDupeCheck once file has been selected
					btnRunDupeCheck.setEnabled(true);
					//btnStop.setEnabled(true);
					btnOpenFile.setEnabled(false);
				}

			}
		});

		btnRunDupeCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				new Thread(new Runnable() {
					public void run() {
						

						
						try {
							CryptoPass decryptPass = new CryptoPass();
							
							Settings settings = new Settings();
							
							String user = (String) settings.getUsername();
							String pass = (String) settings.getPassword();
							String dir = (String) settings.getDirectory();
							
							/**
							JSONParser parser = new JSONParser();
							Object obj = parser.parse(new FileReader(
					                    "src/settings.txt"));
					 
					        JSONObject jsonObject = (JSONObject) obj;
					 
					         String user = (String) jsonObject.get("User");
					         String pass = (String) jsonObject.get("Pass");
					         String directory = (String) jsonObject.get("Directory");
					         
					         */
					         
					         System.out.println("Directory String: " + dir);
					         
									
							avaturePage = openAvature(txtInfo, user, decryptPass.decrypt(pass));
					
							//avaturePage = openAvature(txtInfo, getUsername(), getPassword());
							if(avaturePage != null){
								
								enableButton(getDisplay(), btnOpenFile, false);
								enableButton(getDisplay(), btnRunDupeCheck, false);
								enableButton(getDisplay(), btnStop, true);
								runDupeCheck(medusaWorkbook, avaturePage, txtInfo, btnOpenFile, btnRunDupeCheck, btnStop);
							} else {
								enableButton(getDisplay(), btnOpenFile, false);
								enableButton(getDisplay(), btnRunDupeCheck, true);
								enableButton(getDisplay(), btnStop, false);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				
				
				
				/**
				
				//Check if there is a username and password saved to preferences file
				//while ((getUsername() == null) || (getPassword() == null)){
					Authenticate loginWindow = new Authenticate(shlAvatureUI, 3);
					loginWindow.open();
					btnRunDupeCheck.setEnabled(false);
					//setCredentials(loginWindow.getUsername(), loginWindow.getPassword());
					//avaturePage = openAvature(txtInfo, loginWindow.getUsername(), loginWindow.getPassword());
				//}

				if(!loginWindow.isCancelButton()){
				new Thread(new Runnable() {
					public void run() {
						try {
						
							avaturePage = openAvature(txtInfo, loginWindow.getUsername(), loginWindow.getPassword());
					
							//avaturePage = openAvature(txtInfo, getUsername(), getPassword());
							if(avaturePage != null){
								
								enableButton(getDisplay(), btnOpenFile, false);
								enableButton(getDisplay(), btnRunDupeCheck, false);
								enableButton(getDisplay(), btnStop, true);
								runDupeCheck(medusaWorkbook, avaturePage, txtInfo, btnOpenFile, btnRunDupeCheck, btnStop);
							} else {
								enableButton(getDisplay(), btnOpenFile, false);
								enableButton(getDisplay(), btnRunDupeCheck, true);
								enableButton(getDisplay(), btnStop, false);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				
				} else {
					//Reset loginWindow Cancel button back to false
					loginWindow.setCancelButton(false);
					btnRunDupeCheck.setEnabled(true);
				}
			
				*/
			}
		});

	} // End Create Contents Method
	
	
	// Make copy of medusa file then add new headers to the copied file.  
	// Duplicate Names, Person ID, Candidate Type, Reason for Dupe, Keyword Search URl and NameEmployer Search URL
	public static Workbook getDataSheet(String name, Text txtInfo)
			throws IOException, EncryptedDocumentException, InvalidFormatException {

		FileInputStream file = new FileInputStream(name);
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheetAt(0);
		String numRows = "Number of Users to be processed: " + String.valueOf(sheet.getPhysicalNumberOfRows() - 1);

		Row header = sheet.getRow(0);

		Cell duplicateNames = header.createCell(49);
		duplicateNames.setCellValue("Duplicate Names");
		Cell personID = header.createCell(50);
		personID.setCellValue("Person ID");
		Cell candidateType = header.createCell(51);
		candidateType.setCellValue("Candidate Type");
		Cell reasonDupe = header.createCell(52);
		reasonDupe.setCellValue("Reason for Dupe");
		Cell searchURL = header.createCell(53);
		searchURL.setCellValue("Keyword Search URL");
		Cell nameEmpURL = header.createCell(54);
		nameEmpURL.setCellValue("NameEmployer Search URL");

		file.close();

		FileOutputStream outFile = new FileOutputStream(new File(name));
		wb.write(outFile);
		outFile.close();

		updateInfoBox(getDisplay(), txtInfo, numRows);

		return wb;

	}

	
	// Create an authenticated session using a web driver
	public static WebDriver openAvature(Text txtInfo, String user, String pass) {

		WebDriver driver;
		
		final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		/**
		final LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, java.util.logging.Level.OFF);
		logs.enable(LogType.CLIENT, java.util.logging.Level.OFF);
		logs.enable(LogType.DRIVER, java.util.logging.Level.OFF);
		logs.enable(LogType.PERFORMANCE, java.util.logging.Level.OFF);
		logs.enable(LogType.PROFILER, java.util.logging.Level.OFF);
		logs.enable(LogType.SERVER, java.util.logging.Level.OFF);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
		*/
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		
		System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
		
		
		driver = new ChromeDriver(capabilities);
		//driver = new FirefoxDriver();
		
		String info = "Avature authentication in progress!";
		updateInfoBox(getDisplay(), txtInfo, info);

		// Login Page
		String baseUrl = "https://cisco.avature.net/#People/Id:1/Filters:{%22entityTypeId%22:2,%22set%22:null}";
		
		driver.get(baseUrl);
		

		try {
			WebDriverWait wait = new WebDriverWait(driver, 40);

			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.id("userInput")));

		} catch (NoSuchElementException e2) {

			e2.printStackTrace();
			;
		}

		//driver.findElement(By.id("userInput")).sendKeys("dinho");
		//driver.findElement(By.id("passwordInput")).sendKeys("Welcome1!");
		driver.findElement(By.id("userInput")).sendKeys(user);
		driver.findElement(By.id("passwordInput")).sendKeys(pass);
		driver.findElement(By.id("login-button")).submit();

		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 40);

			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.className("ListSearchInputField")));
			
			info = "Login complete!";
			updateInfoBox(getDisplay(), txtInfo, info);

			return driver;

		} catch (TimeoutException e2) {
		
			
			info = "Login Failed!";
			updateInfoBox(getDisplay(), txtInfo, info);
			driver.close();
			driver.quit();

			return null;

			//e2.printStackTrace();
			//;
		}
		
		

	}

	
	/** Compare Medusa User info to returned Avature users for possible duplicate.  Save results of dupe check to spreadsheet and close web driver on completion
	 * Does two separate Basic Avature searches.  
	 * 1). Keywords - User email's and LinkedIn user name and ID
	 * 2). Full name and Company -  
	 * 
	 * 
	 * @param medusaWB
	 * @param avatureData
	 * @param txtInfo
	 * @param btnOpenFile
	 * @param btnRunDupeCheck
	 * @param btnStop
	 * @throws IOException
	 */
	public static void runDupeCheck(Workbook medusaWB, WebDriver avatureData, Text txtInfo, Button btnOpenFile, Button btnRunDupeCheck, Button btnStop) throws IOException {

		// If more than 50 results and there is the presence of the Next button
		// then continue to next page of results
		// boolean runCheck = true;

		
		Sheet userData = medusaWB.getSheetAt(0);
		loadUserList(userData);

		int rowNum = 1;
		WebDriver avaturePage = avatureData;
		//avatureData.quit();

		// Process each user in the Medusa File
		for (UserInfo mUser : userList) {
			
			// Check to see if Stop button has been pressed
			if(stopButton){
				break;
			}
			//WebDriver avaturePage = avatureData;
			// Update txtInfo Box with currently processed Medusa user
			String info = rowNum + " Of " + userList.size() + ": " + mUser.getFullName();
			updateInfoBox(getDisplay(), txtInfo, info);
						
			//Clear out the List of Avature users
			avatureList.clear();
			String dupeNames = "";
			String candidateTypes = "";
			String dupeReasons = "";
			String personID = "";
			//boolean duplicate = false;
			boolean keywordSearch = false;
			boolean nameEmployerSearch = false;

			Row row = userData.getRow(rowNum);

			// URL that will return custom page with custom columns
			// String searchURL =
			// "https://cisco.avature.net/#People/Id:1844/edit/filters/AdvancedFiltering";
			
			String searchSimpleKeywords = "https://cisco.avature.net/#People/Id:1880/edit/filters/simple";
			String searchSimpleNameEmployer = "https://cisco.avature.net/#People/Id:1881/edit/filters/simple";

			if (mUser.getEmails().isEmpty() && (mUser.getLinkedInUserName() == "")) {
				// No Results were found in both searches so continue to next
				// user
				row.createCell(52).setCellValue("No Records Found");
				row.createCell(53).setCellValue("No Records Found");
			} else {
				keywordSearch = runSearchURL(avaturePage, searchSimpleKeywords, mUser, txtInfo);
			}
			
			//String searchSimpleKeywords = "https://cisco.avature.net/#People/Id:1880/edit/filters/simple";
			//String searchSimpleNameEmployer = "https://cisco.avature.net/#People/Id:1881/edit/filters/simple";

			//keywordSearch = runSearchURL(avaturePage, searchSimpleKeywords, mUser);
			nameEmployerSearch = runSearchURL(avaturePage, searchSimpleNameEmployer, mUser, txtInfo);

			if (!keywordSearch && !nameEmployerSearch) {
				// No Results were found in both searches so continue to next
				// user
				row.createCell(52).setCellValue("No Records Found");
				row.createCell(53).setCellValue("No Records Found");
				rowNum++;
				continue;
			}

			// Run Comparison Check. Comparing Avature results to medusa user
			// determining reason for Duplicate
			for (AvatureInfo aUser : avatureList) {
				boolean duplicate = false;
				// Check LinkedIn ID and UserName for match
				if (!aUser.getWebsites().isEmpty()) {
					// Check if either Linked in Username or ID is in website
					// URLs
					for (String site : aUser.getWebsites()) {
						if (site.contains(mUser.getLinkedInUserName()) || site.contains(mUser.getLinkedInID())) {

							//mUser.appendReasonForDupe("LinkedInProfile Match");
							aUser.setReasonForDupe("LinkedInProfile Match");
							// Update the Strings for Medusa cells
							//dupeNames = dupeNames + aUser.getFullName() + ",";
							//candidateTypes = candidateTypes + aUser.getCandidateType() + ",";
							//dupeReasons = dupeReasons + aUser.getReasonForDupe() + ",";

							// row.createCell(48).setCellValue(aUser.getPersonID());
							// row.createCell(48).setCellValue(aUser.getCandidateType());
							// row.createCell(50).setCellValue("100");
							duplicate = true;
							// withWebsite++;
							break;

						}
					}

				}

				// Check Medusa email against Avature emails
				boolean emailFound = false;
				if (!mUser.getEmails().isEmpty() && !duplicate) {
					if (!aUser.getEmails().isEmpty()) {
						for (String mEmail : mUser.getEmails()) {
							for (String aEmail : aUser.getEmails()) {
								if (mEmail.equalsIgnoreCase(aEmail)) {
									//mUser.appendReasonForDupe("Email Match");
									aUser.setReasonForDupe("Email Match");
									// Update the Strings for Medusa cells
									//dupeNames = dupeNames + aUser.getFullName() + ",";
									//candidateTypes = candidateTypes + aUser.getCandidateType() + ",";
									//dupeReasons = dupeReasons + aUser.getReasonForDupe() + ",";
									emailFound = true;
									duplicate = true;
									break;
								}
							}
							if (emailFound) {
								break;
							}
						}
					}

				}

				// Email or LinkedIn profile search did not show
				if (!duplicate) {
					if(mUser.getEmployer().contains(aUser.getEmployer())) {
						aUser.setReasonForDupe("FullNameAndEmployer");
					} else {
						aUser.setReasonForDupe("Keywords");
					}
				}
				
			}
			
			
			for (AvatureInfo aUser : avatureList) {
				dupeNames = dupeNames + aUser.getFullName().replace(",", " ") + ",";
				personID = personID + aUser.getPersonID() + ",";
				candidateTypes = candidateTypes + aUser.getCandidateType() + ",";
				dupeReasons = dupeReasons + aUser.getReasonForDupe() + ",";
			}
			

			// List of duplicate Names
			row.createCell(49).setCellValue(dupeNames);
			
			// List Person ID's
			row.createCell(50).setCellValue(personID);

			// List of candidate Types
			row.createCell(51).setCellValue(candidateTypes);

			// Reason for Dupe
			row.createCell(52).setCellValue(dupeReasons);
			
			CellStyle hlink_style = medusaWB.createCellStyle();
			Font hlink_font = medusaWB.createFont();
		    hlink_font.setUnderline(Font.U_SINGLE);
		    hlink_font.setColor(IndexedColors.BLUE.getIndex());
		    hlink_style.setFont(hlink_font);


			if (keywordSearch) {
				// Search Keyword URL that produced results for comparison
				Cell searchKeywordCell = row.createCell(53);
				searchKeywordCell.setCellValue("Search URL");

				// Create the Hyperlink to the Search URL
				CreationHelper createHelper = medusaWB.getCreationHelper();
				Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);

				String url = mUser.getSearchKeywordURL().substring(6).replace(":", "%3A").replace("{", "%7B")
						.replace("}", "%7D").replace("\"", "%22").replace(" ", "%20");
				url = "https:" + url;
				System.out.println("Search URL: " + url);

				link.setAddress(url);
				searchKeywordCell.setHyperlink(link);
				searchKeywordCell.setCellStyle(hlink_style);
			} else {
				Cell searchKeywordCell = row.createCell(53);
				searchKeywordCell.setCellValue("No Records Found");

			}

			if (nameEmployerSearch) {

				// Search Name Employer URL that produced results for comparison
				Cell searchCell = row.createCell(54);
				searchCell.setCellValue("Search URL");

				// Create the Hyperlink to the Search URL
				CreationHelper createHelper = medusaWB.getCreationHelper();
				Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);

				String url = mUser.getSearchNameEmployerURL().substring(6).replace(":", "%3A").replace("{", "%7B")
						.replace("}", "%7D").replace("\"", "%22").replace(" ", "%20");
				url = "https:" + url;
				System.out.println("Search URL: " + url);

				link.setAddress(url);
				searchCell.setHyperlink(link);
				searchCell.setCellStyle(hlink_style);
			} else {
				Cell searchKeywordCell = row.createCell(54);
				searchKeywordCell.setCellValue("No Records Found");

			}
			//avaturePage.quit();
			//avaturePage.close();

			rowNum++;

		} // End for(UserInfo mUser : userList) loop

		
		
		//FileOutputStream outFile = new FileOutputStream(new File(outMedusa));
		//medusaWB.write(outFile);
		//outFile.close();
		
		/**
		LocalStorage local = ((WebStorage) avaturePage).getLocalStorage();
		local.clear();
		*/
		
		
		//avaturePage.close();
		//avaturePage.quit();
		
		String info = "Saving to Output file";
		
		updateInfoBox(getDisplay(), txtInfo, info);
		
		//Save to Excel and close the browser
		saveExcel(medusaWB, avaturePage);

		System.out.println("number of Rows: " + rowNum);
		
		info = "Duplicate Check complete!";
		
		initButtons(getDisplay(), btnOpenFile, btnRunDupeCheck, btnStop);
		
		updateInfoBox(getDisplay(), txtInfo, info);
	}
	
	// Write all user results to Checked Medusa file and reset all user Lists
	public static void saveExcel(Workbook workbook, WebDriver driver) throws IOException{
		
		FileOutputStream outFile = new FileOutputStream(new File(outMedusa));
		workbook.write(outFile);
		outFile.close();
		
		/**
		LocalStorage local = ((WebStorage) avaturePage).getLocalStorage();
		local.clear();
		*/
		
		
		// Close the web browser
		driver.close();
		driver.quit();
		
		//Clear all users for next process
		userList.clear();
		avatureList.clear();
		
		// If stopButton was pressed then reset it back to false
		stopButton = false;
		
	}

	// Populate userList with information from medusa file
	public static void loadUserList(Sheet userData) {

		// Create object UserInfo for each row from data sheet
		int numRows = userData.getLastRowNum();
		for (int i = 1; i <= numRows; i++) {
			UserInfo user = new UserInfo();
			Row row = userData.getRow(i);
			
			
			user.setFullName(getCellValue(row, 1));

			// Set the user's employer value
			user.setEmployer(getCellValue(row, 14));

			user.setLinkedInProfile(getCellValue(row, 37));
			user.setLinkedInRecruiter(getCellValue(row, 38));

			// Get LinkedIn UserName from LinkedInProfile url
			user.setLinkedInUserName(user.getLinkedInProfile());
			// Get LinkedIn ID from LinkedInRecruiter url
			user.setLinkedInID(user.getLinkedInRecruiter());

			// Get the User's emails
			String homeEmail = getCellValue(row, 24);
			System.out.println("Home Email: " + homeEmail);
			if ((!homeEmail.equals("Gmail.com") && homeEmail != "")) {
				user.appendEmails(homeEmail);
			}

			// Get the User's emails
			String workEmail = getCellValue(row, 25);
			System.out.println("Work Email: " + workEmail);
			if ((!workEmail.equals("yahoo.com") && workEmail != "")) {
				user.appendEmails(workEmail);
			}

			// Get the User's emails
			String additionalEmail = getCellValue(row, 26);
			System.out.println("Additional Email: " + additionalEmail);
			if ((!additionalEmail.equals("outlook.com") && additionalEmail != "")) {
				user.appendEmails(additionalEmail);
			}


			System.out.println("User Name: " + user.getFullName());
			System.out.println("User Employer: " + user.getEmployer());
			System.out.println("User LinkedProfile: " + user.getLinkedInProfile());
			System.out.println("User LinkedRecruiter: " + user.getLinkedInRecruiter());
			System.out.println("User UserName: " + user.getLinkedInUserName());
			System.out.println("User LinkedInID " + user.getLinkedInID());
			userList.add(user);
		}

	}

	private static boolean runSearchURL(WebDriver avatureData, String searchURL, UserInfo mUser, Text txtInfo) {

		String keywords = "";
		boolean keywordURL = false;
		WebDriver avaturePage = avatureData;
		
		//Test chrome in new tab
		
		//((JavascriptExecutor) avaturePage).executeScript("window.open('','_blank');");

	    //ArrayList<String> tabs = new ArrayList<String> (avaturePage.getWindowHandles());
	   // avaturePage.switchTo().window(tabs.get(1)); //switches to new tab
	    
		avaturePage.get(searchURL);

		
		if (searchURL.contains("1880")) {

			// Construct email search string
			String emailSearch = "";
			if (!mUser.getEmails().isEmpty()) {
				int numEmails = mUser.getEmails().size();
				switch (numEmails) {
				case 1:
					emailSearch = mUser.getEmails().get(0);
					break;
				case 2:
					emailSearch = mUser.getEmails().get(0) + " OR " + mUser.getEmails().get(1);
					break;
				case 3:
					emailSearch = mUser.getEmails().get(0) + " OR " + mUser.getEmails().get(1) + " OR "
							+ mUser.getEmails().get(2);
					;
					break;

				}

			}

			// String linkedIn = mUser.getLinkedInID() + " OR " +
			// mUser.getLinkedInUserName();
			List<String> linkedInUser = new ArrayList<String>();
			String linkedInUserName = mUser.getLinkedInUserName();
			String linkedInID = mUser.getLinkedInID();
			String linkedIn = "";
			
			if (linkedInUserName != "") {
				linkedInUser.add(linkedInUserName);
			} 
			
			if (linkedInID != ""){
				linkedInUser.add(linkedInID);
			}
			
			switch (linkedInUser.size()){
			case 0:
				linkedIn = "";
				break;
			case 1:
				linkedIn = linkedInUser.get(0);
				break;
			case 2:
				linkedIn = linkedInUser.get(0) + " OR " + linkedInUser.get(1);
				break;
			}
			
			
			// Construct the LinkedIn search
			
			//if (linkedInUserName == "")
			
			

			if (emailSearch != "" && linkedIn != "") {
				keywords = emailSearch + " OR " + linkedIn;
			} else if (emailSearch != "" && linkedIn == ""){
				keywords = emailSearch;
			} else if (emailSearch != ""){
				keywords = emailSearch;
			} else {
				keywords = linkedIn;
			}
			
			try {
				WebDriverWait wait = new WebDriverWait(avaturePage, 50);

				wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//textarea[@class='resizableInput widget resizableTextarea ICO_ResizableTextarea']")));

			} catch (TimeoutException e2) {

				String info = "Timeout Exceptioin Error 1";
				updateInfoBox(getDisplay(), txtInfo, info);
				
				//Close tab and switch back to main window
				//avaturePage.close();
				//avaturePage.switchTo().window(tabs.get(0)); // switch back to main screen
				return false;
				//e2.printStackTrace();
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			// Keyword search string consist of emails and linkedin username
			avaturePage
					.findElement(By
							.xpath("//textarea[@class='resizableInput widget resizableTextarea ICO_ResizableTextarea']"))
					.clear();
			avaturePage
					.findElement(By
							.xpath("//textarea[@class='resizableInput widget resizableTextarea ICO_ResizableTextarea']"))
					.sendKeys(keywords);

			keywordURL = true;

		} else {
			
			try {
				WebDriverWait wait = new WebDriverWait(avaturePage, 40);

				wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//span[@class='uicore_advancedselect_AdvancedSelect_optGroup']")));

			} catch (TimeoutException e2) {

				String info = "Timeout Exceptioin Error 2";
				updateInfoBox(getDisplay(), txtInfo, info);
				
				//Close tab and switch back to main window
				//avaturePage.close();
				//avaturePage.switchTo().window(tabs.get(0)); // switch back to main screen
				
				return false;
				//e2.printStackTrace();
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// FullName and Employer Search
			WebElement searchTable = avaturePage.findElement(By.className("RowEditorTable"));
			List<WebElement> tableRows = searchTable.findElements(By.className("RowEditorRow"));

			// Determine the row where fullName input box is
			WebElement nameField = tableRows.get(0).findElement(By.className("value"));
			nameField.findElement(By.tagName("input")).clear();
			nameField.findElement(By.tagName("input")).sendKeys(mUser.getFullName());

			// Determine the row where company name input box is
			WebElement employerField = tableRows.get(1).findElement(By.className("value"));
			// employerField.findElement(By.className("AdvancedSelectSelectedItemRemoveLink")).click();
			employerField.findElement(By.tagName("input")).sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE);
			employerField.findElement(By.tagName("input")).sendKeys(mUser.getEmployer());

		}

		System.out.println("Testing Apply Button Value: "
				+ avaturePage.findElement(By.xpath("//button[text()='Apply']")).getText());

		try {
		// Hit the apply button to do search
		avaturePage.findElement(By.xpath("//button[text()='Apply']")).click();
		} catch (WebDriverException e1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			avaturePage.findElement(By.xpath("//button[text()='Apply']")).click();
		}
		

		
		try {
			// Wait for Search results page finishes loading. Set at 50 seconds
			WebDriverWait wait = new WebDriverWait(avaturePage, 120);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ListSearchInputField")));
			} catch (TimeoutException e2) {

				//e2.printStackTrace();
				String info = "Timeout Exceptioin Error 3";
				updateInfoBox(getDisplay(), txtInfo, info);
				return false;
			}

		// Get the Results page URL
		if (keywordURL) {
			mUser.setSearchKeywordURL(avaturePage.getCurrentUrl());
		} else {
			mUser.setSearchNameEmployerURL(avaturePage.getCurrentUrl());
		}

		try {
			// User Search return no results so no duplicate
			avaturePage.findElement(By.className("uicore_list_NoResultsMessage"));
			return false;
			

		} catch (NoSuchElementException e1) {

			// e1.printStackTrace();
			;

		}

		String pageSource = avaturePage.getPageSource();

		Document soupData = Jsoup.parse(pageSource);

		Element table = soupData.select("tbody[class=uicore_table_Base_ResultList]").get(0);
		Elements rows = table.select("tr");

		System.out.println("Number of Rows: " + rows.size());

		loadAvatureList(rows);
		
		/**
		LocalStorage local = ((WebStorage) avaturePage).getLocalStorage();
		local.clear();
		*/
		//Close tab and switch back to main window
		//avaturePage.close();
		//avaturePage.switchTo().window(tabs.get(0)); // switch back to main screen
		
		return true;

	}

	public static void loadAvatureList(Elements rows) {

		for (int i = 0; i < rows.size(); i++) { // first row is the col
			// names so skip it.
			boolean userMatch = false;
			Element r = rows.get(i);
			Elements cols = r.select("td");

			AvatureInfo avatureUser = new AvatureInfo();

			// Set Full name
			avatureUser.setFullName(cols.get(1).text());
			
			// Set Employer
			avatureUser.setEmployer(cols.get(3).text());

			// Retrieve all the listed websites for comparison
			Elements sites = r.select("a[class=WebsitesContactInfoViewerLink]");
			for (Element site : sites) {
				avatureUser.appendWebsites(site.attr("href"));
				System.out.println("Website: " + site.attr("href"));
			}

			Elements emails = r.select("span[class=emailField]");
			for (Element email : emails) {
				avatureUser.appendEmails(email.text());
				System.out.println("Emails: " + email.text());
			}

			// Set Candidate Type
			avatureUser.setCandidateType(cols.get(6).text());
			System.out.println("Cadidate Type: " + cols.get(6).text());

			// Set PersonID
			avatureUser.setPersonID(cols.get(8).text());
			System.out.println("Person ID: " + cols.get(8).text());

			// Check if user is already in the list from previous search
			for (AvatureInfo existingUser : avatureList) {
				if (existingUser.getPersonID().equals(avatureUser.getPersonID())) {
					userMatch = true;
					break;
				} else {
					continue;
				}
			}

			if (!userMatch) {
				avatureList.add(avatureUser);
			}

		}

	}
	
	private static String getCellValue(Row row, int index){
		Cell cell = row.getCell(index);
		if (cell != null){
			return cell.getStringCellValue();
		} else {
			return "";
		}
		
	}

	private static void updateInfoBox(final Display display, Text txtInfo, String info) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!txtInfo.isDisposed()) {
					txtInfo.append("\n" + info);
					txtInfo.getParent().layout();
				}
			}
		});

	}
	
	private static void initButtons(final Display display, Button btnOpenFile, Button btnRunDupeCheck, Button btnStop) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				btnOpenFile.setEnabled(true);
				btnRunDupeCheck.setEnabled(false);
				btnStop.setEnabled(false);
			}
		});

	}
	
	private static void enableButton(final Display display, Button button, boolean value) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				button.setEnabled(value);
			}
		});

	}


	// Get the current GUI display to be used for setLableContactsProcessed and
	// msgBoxCompletion methods
	public static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	private void createSettingsFile() {
		
		JSONObject settingsObj = new JSONObject();
		
		settingsObj.put("User",  "");
		settingsObj.put("Pass",  "");
		settingsObj.put("Directory", "src/");
		
		try (FileWriter file = new FileWriter("src/settings.txt")) {
			file.write(settingsObj.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
