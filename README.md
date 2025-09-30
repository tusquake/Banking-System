# Banking SOAP Web Service (Spring Boot)

A complete **SOAP API** implementation for banking operations using Spring Boot.  
Supports creating accounts, fetching account details, deposits, withdrawals, and transfers.

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/bankingsoap/
│   │       ├── BankingSoapApplication.java
│   │       ├── config/
│   │       │   └── WebServiceConfig.java
│   │       └── endpoint/
│   │           └── AccountEndpoint.java
│   └── resources/
│       ├── xsd/
│       │   ├── banking.xsd
│       │   └── transactions.xsd
│       └── application.properties
└── pom.xml
```

---

## Setup Instructions

### 1. Create the Project Structure
Create folders exactly as shown above.

### 2. Add XSD Schemas
Place `banking.xsd` and `transactions.xsd` in `src/main/resources/xsd/`.

### 3. Generate Java Classes from XSD
Run the Maven build to generate Java classes:

```bash
mvn clean compile
```

Generated classes will be in the package:  
`com.bank.bankingsoap.generated`

### 4. Run the Application

```bash
mvn spring-boot:run
```

SOAP service will start at:
```
http://localhost:8080/ws
```

---

## Accessing the WSDL

```
http://localhost:8080/ws/banking.wsdl
```

---

## SOAP Operations & Sample Requests

Namespace for all requests: `http://bank.com/banking`

### 1. Create Account

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bank="http://bank.com/banking">
   <soapenv:Header/>
   <soapenv:Body>
      <bank:createAccountRequest>
         <bank:accountHolderName>John Doe</bank:accountHolderName>
         <bank:accountType>SAVINGS</bank:accountType>
         <bank:initialBalance>1000.00</bank:initialBalance>
      </bank:createAccountRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 2. Get Account

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bank="http://bank.com/banking">
   <soapenv:Header/>
   <soapenv:Body>
      <bank:getAccountRequest>
         <bank:accountNumber>1234567890</bank:accountNumber>
      </bank:getAccountRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 3. Deposit

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bank="http://bank.com/banking">
   <soapenv:Header/>
   <soapenv:Body>
      <bank:depositRequest>
         <bank:accountNumber>1234567890</bank:accountNumber>
         <bank:amount>500.00</bank:amount>
      </bank:depositRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 4. Withdraw

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bank="http://bank.com/banking">
   <soapenv:Header/>
   <soapenv:Body>
      <bank:withdrawRequest>
         <bank:accountNumber>1234567890</bank:accountNumber>
         <bank:amount>200.00</bank:amount>
      </bank:withdrawRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 5. Transfer

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bank="http://bank.com/banking">
   <soapenv:Header/>
   <soapenv:Body>
      <bank:transferRequest>
         <bank:fromAccountNumber>1234567890</bank:fromAccountNumber>
         <bank:toAccountNumber>9876543210</bank:toAccountNumber>
         <bank:amount>150.00</bank:amount>
      </bank:transferRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

---

## Testing with cURL

Example: **Get Account**

```bash
curl -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: " \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:bank="http://bank.com/banking">
        <soapenv:Header/>
        <soapenv:Body>
            <bank:getAccountRequest>
                <bank:accountNumber>1234567890</bank:accountNumber>
            </bank:getAccountRequest>
        </soapenv:Body>
      </soapenv:Envelope>'
```

---

## Sample Data

- **Account 1:** 1234567890, Name: John Doe, Type: SAVINGS, Balance: 1000.00
- **Account 2:** 9876543210, Name: Jane Smith, Type: CURRENT, Balance: 5000.00

> Replace in-memory data with a Spring Data JPA repository for persistent storage.

---

## Customization

### Add More Operations
1. Update your XSD files with new request/response elements.
2. Run `mvn compile` to regenerate Java classes.
3. Implement new methods in your endpoint class (`AccountEndpoint.java`) using `@PayloadRoot`.

### Connect to Database

Replace in-memory maps with a **Spring Data JPA repository** for persistence.

---

## Key Components

- **XSD Schemas:** Define the SOAP message structure (`banking.xsd`, `transactions.xsd`)
- **Endpoint:** Handles requests using `@PayloadRoot`
- **WebServiceConfig:** Configures SOAP service and WSDL exposure
- **MessageDispatcherServlet:** Routes SOAP requests to endpoints

---
