// set up Express
var express = require('express');
var app = express();
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/myDatabase');

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Account class from Account.js
var Account = require('./Account.js');

/***************************************/

app.post('/login', (req, res) => {
	var name = req.body.username;
	var password = req.body.password;
	Account.findOne({username: name}, function (err, account) {
		if (err || account == null) {		//Account doesn't exist
			console.log("Invalid Username");
		} else {
			console.log(account);
			if (password == account.password) { //Account exists and pswd matches
				console.log("Welcome Back!");
			} else {							//Accoutn exists but incorrect pswd
				console.log("Incorrect password");
			}
		}
	})
	/* Code block print all documents to the console
	Account.find(function (err, accounts) {
	  	if (err) return console.error(err);
	  	console.log(accounts);
	})*/ 
	console.log(name + " " + password);
})

// route for creating a new person
// this is the action of the "create new person" form
app.use('/register', (req, res) => {
	// construct the Person from the form data which is in the request body
	var newAccount = new Account ({
		username: req.body.username,
		lastname: req.body.lastname,
		firstname: req.body.firstname,
		password: req.body.password,
	    });

	console.log(newAccount.username + " " + newAccount.firstname + " " + newAccount.lastname + " "  + newAccount.password);

	// save the account to the database
	newAccount.save( (err) => { 
		if (err) {
		    //res.type('html').status(200);
		    //res.write('uh oh: ' + err);
		    if (err.message.includes("duplicate")) {
		    	console.log("Username already exists.")
		    } else {
		    	console.log(err);
		    }
		    res.end();
		}
		else {
		    // display the "successfull created" page using EJS
		    //res.render('created', {account : newAccount});
		}
	    } ); 
    }
    );

// route for showing all the people
/*
app.use('/all', (req, res) => {
    
	// find all the Person objects in the database
	Person.find( {}, (err, persons) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
		    if (persons.length == 0) {
			res.type('html').status(200);
			res.write('There are no people');
			res.end();
			return;
		    }
		    // use EJS to show all the people
		    res.render('all', { persons: persons });

		}
	    }).sort({ 'age': 'asc' }); // this sorts them BEFORE rendering the results
    });*/

// route for accessing data via the web api
// to use this, make a request for /api to get an array of all Person objects
// or /api?name=[whatever] to get a single object
app.use('/api', (req, res) => {
	console.log("LOOKING FOR SOMETHING?");

	// construct the query object
	var queryObject = {};
	if (req.query.username) {
	    // if there's a name in the query parameter, use it here
	    queryObject = { "username" : req.query.username };
	}
    
	Account.find( queryObject, (err, accounts) => {
		console.log(accounts);
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else if (accounts.length == 0) {
		    // no objects found, so send back empty json
		    res.json({});
		}
		else if (accounts.length == 1 ) {
		    var account = accounts[0];
		    // send back a single JSON object
		    res.json( { "username" : person.name , "firstname" : account.firstname, "lastname" : account.lastname } );
		}
		else {
		    // construct an array out of the result
		    var returnArray = [];
		    accounts.forEach( (account) => {
			    returnArray.push( { "username" : person.name , "firstname" : account.firstname, "lastname" : account.lastname } );
			});
		    // send it back as JSON Array
		    res.json(returnArray); 
		}
		
	    });
    });




/*************************************************/

app.use('/public', express.static('public'));

//app.use('/', (req, res) => { res.redirect('/public/personform.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
