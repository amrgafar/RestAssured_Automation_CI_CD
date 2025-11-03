This project contains automated API tests for the Bosta Quality Engineer Assessment. It uses Rest Assured (Java) to perform functional, validation, and security tests against the provided staging APIs.

The tests are integrated with a GitHub Actions CI/CD pipeline, which automatically executes the test suite on every push and pull request.

🧪 Test Scope
The test suite focuses on "thinking like an ethical hacker"  to find vulnerabilities and bugs. Tests are implemented for the following endpoints:


API #1: Create Pickup (POST /api/v2/pickups) 


P-LOGIC-01: Tests for negative numberOfParcels to prevent a "credit" bug.


API #2: Update Bank Info (POST /api/v2/businesses/add-bank-info) 


B-LOGIC-01: Tests for attempting to update info without an OTP.


API #3: Forget Password (POST /api/v2/users/forget-password) 


F-PARAM-01: Tests for sending an empty JSON body ({}) to check for unhandled errors.

⚙️ Prerequisites
To run this project, you will need:

Java (JDK 11 or newer)

Apache Maven

An IDE (like IntelliJ IDEA or Eclipse)

🚀 Setup & Installation
Clone the repository:

Bash

git clone <your-repo-url>
cd bosta-api-tests
Install dependencies: Maven will handle this automatically when you build or run the tests.

▶️ How to Run Tests
There are two ways to run these tests.

1. Running Locally (in an IDE)
IMPORTANT: The tests will fail with a java.lang.IllegalArgumentException: Header value cannot be null if you do not set the required API tokens.

You must set the following environment variables in your IDE's "Run Configuration":


BOSTA_PICKUP_TOKEN: The token for the pickup API .


BOSTA_BANK_TOKEN: The token for the bank info API .

After setting the variables, you can run the tests:

Via IDE: Right-click BostaApiSecurityTests.java and select "Run".

Via Terminal: mvn test

2. Running via CI/CD (GitHub Actions)
This project is configured with a GitHub Actions workflow in .github/workflows/api-tests.yml. The workflow automatically runs mvn test on every push and pull request to the main branch.

Setup for CI/CD:

For the pipeline to run successfully, you must add the API tokens to your GitHub repository's secrets:

Go to your repository > Settings > Secrets and variables > Actions.

Click "New repository secret" and create the following two secrets:

Name: BOSTA_PICKUP_TOKEN


Value: (Paste the token from )

Name: BOSTA_BANK_TOKEN


Value: (Paste the token from )

The workflow will securely inject these secrets as environment variables, allowing your tests to authenticate and run successfully.
