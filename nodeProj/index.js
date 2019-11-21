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
var Convo = require('./Convo.js');
var Message = require('./Message');

/***************************************/

// need the http server instance for socket.io
var server = app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
var io = require('socket.io').listen(server);

io.on('connection', (socket)=> {
	console.log('a user connected');
	socket.on('join', (data) => {
		console.log(data);

		dataJson = JSON.parse(data);

		console.log('username: ' + dataJson.username);
	});
});

app.post('/login', (req, res) => {
	var name = req.body.username;
	var password = req.body.password;
	Account.findOne({username: name}, function (err, account) {
		if (err || account == null) {		//Account doesn't exist
			res.json({});
			//console.log("Invalid Username");
		} else {
			//console.log(account);
			if (password == account.password) { //Account exists and pswd matches
				res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies});
				//console.log("Welcome Back!");
			} else {
				res.json({});							//Accoutn exists but incorrect pswd
				//console.log("Incorrect password");
			}
		}
	});
	/* Code block print all documents to the console
	Account.find(function (err, accounts) {
	  	if (err) return console.error(err);
	  	console.log(accounts);
	})*/ 
	console.log(name + " " + password);

	//res.json({"username":"hardCodedTest"});
});

app.use('/deleteacc', (req, res) => {
	username = req.body.username;
	Account.remove({username : username}, function(err) {
		if (err) {
			console.log(err);
			console.log("Error with account deletion");
		} else {
			console.log("Account deleted");
		}
	});
});

// route for creating a new person
// this is the action of the "create new person" form
app.use('/register', (req, res) => {
	var username = req.body.username;

	Account.findOne({username: username}, function (err, account) {
		if (err) {
			console.log("Create account, unique username validation error");
			res.send("false");
		}
		if (account != null) {
			res.send("false");
		} else {
			res.send("true");
		}
	});
});	

app.post('/food_preferences', (req, res) => {

	var newAccount = new Account ({
			username: req.body.username,
			firstname: req.body.firstname,
			lastname: req.body.lastname,
			password: req.body.password,
			restrictions: req.body.restrictions,
			allergies: req.body.allergies
	    });

	newAccount.save(function (err) {
		if (err) {
			console.log("Error saving new account to database");
			console.log(err);
			res.json({});
		} else {
			console.log("Account saved correctly");
			res.json({"username": newAccount.username, "firstname": newAccount.firstname, "lastname": newAccount.lastname, "restrictions": newAccount.restrictions, "allergies": newAccount.allergies});
		}
	});
	/*Account.findOne({username: name}, function (err, account) {
		if (err || account == null) {		//Account doesn't exist
			console.log("Invalid Username");
		} else {
			console.log("reached");
			account.preferences = preferenceArr;
			account.allergies = allergyArr;

			console.log(account.username + " " + account.preferences + " " + account.allergies);

			account.save( (err) => {
				if (err) {
					console.log(err);
					res.end();
				} else {
					//preferences successfully updated
				}
			});
		}
	});*/
});

app.use('/edit_account', (req, res) => {
	Account.updateOne({username: req.body.username}, {$set:{restrictions: req.body.restrictions, allergies: req.body.allergies}}, function (err, account) {
		if (err) {
			console.log("Edit account error");
			res.json({});
		} else {
			console.log("Account successfully edited")
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies});
		}
	});
	
});

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

app.use('/convos', (req, res) => {
	var queryObj = {};

	// need to figure out how this request looks
	if (req.body.username) {
		queryObj = {"participants": req.body.username};
	}

	Convo.find( queryObj,
    function(err, convos) {
    	if (err) {
    		console.log(err);
    		res.json({});
    	}
    	else {
    		var returnArray = [];
    		convos.forEach((convo) => {
    			console.log(convo);

    			returnArray.push( {"convo_id" : convo.convo_id, "participants" : convo.participants});
    		});

    		res.json(returnArray);
    	}
    } );
});

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
