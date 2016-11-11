# Table of Contents

1. [Challenge Summary] (README.md#challenge-summary)
2. [My Implementation] (README.md#my-implementation)
3. [Description of Data] (README.md#description-of-data)
4. [Repo directory structure] (README.md#repo-directory-structure)
5. [Testing your directory structure and output format] (README.md#testing-your-directory-structure-and-output-format)

##Challenge Summary

Imagine you're a data engineer at a "digital wallet" company called PayMo that allows users to easily request and make payments to other PayMo users. I implemented the following three features to prevent fraudulent payment requests from untrusted users. 

###Feature 1
When anyone makes a payment to another user, they'll be notified if they've never made a transaction with that user before.

* "unverified: You've never had a transaction with this user before. Are you sure you would like to proceed with this payment?"

###Feature 2
The PayMo team is concerned that these warnings could be annoying because there are many users who haven't had transactions, but are still in similar social networks. 

For example, User A has never had a transaction with User B, but both User A and User B have made transactions with User C, so User B is considered a "friend of a friend" for User A.

For this reason, User A and User B should be able to pay each other without triggering a warning notification since they're "2nd degree" friends. 

<img src="./images/friend-of-a-friend1.png" width="500">

To account for this, PayMo would like you to also implement this feature. When users make a payment, they'll be notified when the other user is outside of their "2nd-degree network".

* "unverified: This user is not a friend or a "friend of a friend". Are you sure you would like to proceed with this payment?"


###Feature 3
More generally, PayMo would like to extend this feature to larger social networks. Implement a feature to warn users only when they're outside the "4th degree friends network".

<img src="./images/fourth-degree-friends2.png" width="600">

In the above diagram, payments have transpired between User

* A and B 
* B and C 
* C and D 
* D and E 
* E and F

Under this feature, if User A were to pay User E, there would be no warning since they are "4th degree friends". 

However, if User A were to pay User F, a warning would be triggered as their transaction is outside of the "4th-degree friends network."

(Note that if User A were to pay User C instead, there would be no warning as they are "2nd-degree" friends and within the "4th degree network") 



##My implementation

[Back to Table of Contents] (README.md#table-of-contents)

###Dependencies

Java 1.8.0_101

###Input

As a result, we may assume that collecting the payments has been done and the data resides in two comma-delimited files in the `paymo_input` directory. 

The first file, `batch_payment.txt`, contains past data that can be used to track users who have previously paid one another. These transactions should be used to build the initial state of the entire user network.

Data in the second file, `stream_payment.txt` should be used to determine whether there's a possibility of fraud and a warning should be triggered.

Assume that each new line of `stream_payment.txt` corresponds to a new, valid PayMo payment record -- regardless of being 'unverified' -- and design program to handle a text file with a large number of payments.

###Output

My code processed each line in `stream_payment.txt` and for each payment, output a line containing one of two words, `trusted` or `unverified`. 

`trusted` means the two users involved in the transaction have previously paid one another (when implementing Feature 1) or are part of the "friends network" (when implementing Feature 2 and 3).

`unverified` means the two users have not previously been involved in a transaction (when implementing Feature 1) or are outside of the "friends network" (when implementing Features 2 and 3)

The output should be written to a text file in the `paymo_output` directory. Because we are asking you to implement a minimum of three features, your program should output to at least three text files in the `paymo_output` directory. 

Each output file should be named after the applicable feature you implemented (i.e., `output1.txt`, `output2.txt` and `output3.txt`)

For example, if there were 5 lines of transactions in the `stream_payment.txt`, then the following `output1.txt` file for Feature 1 could look like this: 

	trusted
	trusted
	unverified
	unverified
	trusted

###Data Structures

Undirected graph: We use a undirected graph to represent a past social network which contains the payments history of past users. The two users are connected if they have paid each other.

HashMap: The undirected graph is represented by a HashMap in Java. The key in HashMap is the user that have paid others or have been paid from others before, and the value is a HashSet of users who have paid to or have been paid from this corresponding user in key.

HashSet: The HashSet as a value in HashMap stores all the neighbors connected to one user. The HashSets in BFS store all the users of current level or can be used to store the users who have been visited.

###Explanation of Algorithm

Read all the transactions from batch_payment.txt to initialize the graph. For each transaction, since the graph is undirected, we need to add user1 to the neighbors of user2 and add user2 to the neighbors of user1.

Read one transaction each time from stream_payment.txt and process it. user1 and user2 are users in this transaction.

	Feature 1: check if two users are neighbors in the graph. Get the set of neighbors connected to user1 from graph HashMap and check if user2 is in that set. If not, warning triggers.

	Feature 2: check if two users are connected to one common user. Get the set of neighbors connected to user1. For each neighbor of user1, check if it is in the set of neighbors connected to user2. If not, warning triggers. 

	Feature 3: check if two users are connected within 4 degree by using two-way BFS. Use a begin set which is initialized by user1 and an end set which is initialized by user2. Traverse the partial graph from user1 and user2 to check if they have common neighbors within 4 levels. If not, warning triggers.
	
Write the results to output files.

##Description of Data

[Back to Table of Contents] (README.md#table-of-contents)

The `batch_payment.txt` and `stream_payment.txt` input files are formatted the same way.

As you would expect of comma-separated-value files, the first line is the header. It contains the names of all of the fields in the payment record. In this case, the fields are 

* `time`: Timestamp for the payment 
* `id1`: ID of the user making the payment 
* `id2`: ID of the user receiving the payment 
* `amount`: Amount of the payment 
* `message`: Any message the payer wants to associate with the transaction

Following the header, assume each new line contains a single new PayMo payment record with each field delimited by a comma. In some cases, the field can contain Unicode as PayMo users are fond of placing emojis in their messages. For simplicity's sake, you can choose to ignore those emojis.

For example, the first 10 lines (including the header) of `batch_payment.txt` or `stream_payment.txt` could look like: 

	time, id1, id2, amount, message
	2016-11-02 09:49:29, 52575, 1120, 25.32, Spam
	2016-11-02 09:49:29, 47424, 5995, 19.45, Food for 🌽 😎
	2016-11-02 09:49:29, 76352, 64866, 14.99, Clothing
	2016-11-02 09:49:29, 20449, 1552, 13.48, LoveWins
	2016-11-02 09:49:29, 28505, 45177, 19.01, 🌞🍻🌲🏔🍆
	2016-11-02 09:49:29, 56157, 16725, 4.85, 5
	2016-11-02 09:49:29, 25036, 24692, 20.42, Electric
	2016-11-02 09:49:29, 70230, 59830, 19.33, Kale Salad
	2016-11-02 09:49:29, 63967, 3197, 38.09, Diner
	 
##Repo directory structure
[Back to Table of Contents] (README.md#table-of-contents)

Example Repo Structure

	├── README.md 
	├── run.sh
	├── src
	│  	└── Antifraud.java
	├── paymo_input
	│   └── batch_payment.txt
	|   └── stream_payment.txt
	├── paymo_output
	│   └── output1.txt
	|   └── output2.txt
	|   └── output3.txt
	└── insight_testsuite
	 	   ├── run_tests.sh
		   └── tests
	        	└── test-1-paymo-trans
        		│   ├── paymo_input
        		│   │   └── batch_payment.txt
        		│   │   └── stream_payment.txt
        		│   └── paymo_output
        		│       └── output1.txt
        		│       └── output2.txt
        		│       └── output3.txt
        		└── my-own-test
            		 ├── paymo_input
        		     │   └── batch_payment.txt
        		     │   └── stream_payment.txt
        		     └── paymo_output
        		         └── output1.txt
        		         └── output2.txt
        		         └── output3.txt


##Testing directory structure and output format
[Back to Table of Contents] (README.md#table-of-contents)

The tests are stored simply as text files under the `insight_testsuite/tests` folder. Each test should have a separate folder and each should contain a `paymo_input` folder -- where `batch_payment.txt` and `stream_payment.txt` files can be found. There also should be a `paymo_output` folder where `output1.txt`, `output2.txt` and `output3.txt` should reside.

From the `insight_testsuite` folder, run the test with the following command:

	insight_testsuite$ ./run_tests.sh 

The output of `run_tests.sh` should look like:

    	Reading batch_payment...
	Finished reading batch_payment...0.001s
	Processing record...
	Finished processing all records...0.0s
    	[PASS]: test-1-paymo-trans (output1.txt)
    	[FAIL]: test-1-paymo-trans (output2.txt)
    	1c1
    	< trusted
    	---
    	> unverified
    	[PASS]: test-1-paymo-trans (output3.txt

on failed tests and	
	
	Reading batch_payment...
	Finished reading batch_payment...0.001s
	Processing record...
	Finished processing all records...0.001s
	[PASS]: my-own-test (output1.txt)
	[PASS]: my-own-test (output2.txt)
	[PASS]: my-own-test (output3.txt)
	Reading batch_payment...
	Finished reading batch_payment...0.001s
	Processing record...
	Finished processing all records...0.0s
	[PASS]: test-1-paymo-trans (output1.txt)
	[PASS]: test-1-paymo-trans (output2.txt)
	[PASS]: test-1-paymo-trans (output3.txt)

on success.
