// set up Express
var express = require('express');
var app = express();
var fs = require('fs');
var mongoose = require("mongoose");
var Grid = require('gridfs-stream');
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Account class from Account.js
var Account = require('./Account.js');
var Convo = require('./Convo.js');
var Message = require('./Message');

var Food = require('./Food.js');

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
			console.log("Invalid Username");
		} else {
			console.log(account);
			if (password == account.password) { //Account exists and pswd matches
				res.json({"username":account.username, "password":account.password, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders});
				console.log("Welcome Back!");
			} else {
				res.json({});							//Accoutn exists but incorrect pswd
				console.log("Incorrect password");
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
			res.send({});
		} else {
			console.log("Account deleted");
			res.send({});
		}
	});

	Food.remove({sellerUserName:username}, function(err) {
		if (err) {
			console.log(err);
			console.log("Error deleting all associated posts");
		} else {
			console.log("Deleted all associated posts");
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
			allergies: req.body.allergies,
			orders: req.body.orders
	    });

	newAccount.save(function (err) {
		if (err) {
			console.log("Error saving new account to database");
			console.log(err);
			res.json({});
		} else {
			console.log("Account saved correctly");
			res.json({"username": newAccount.username, "firstname": newAccount.firstname, "lastname": newAccount.lastname, "restrictions": newAccount.restrictions, "allergies": newAccount.allergies, "orders":newAccount.orders});
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
	/*await Account.updateOne({username: req.body.username}, {$set:{restrictions: req.body.restrictions, allergies: req.body.allergies}}, function (err, account) {
		if (err) {
			console.log("Edit account error");
			res.json({});
		} else {
			console.log("Account successfully edited");
		}
	});*/



	Account.findOne({username:req.body.username}, function(err, account) {
		if (err) {
			console.log(err);
		} else {
			console.log("found");
			account.restrictions = req.body.restrictions;
			account.allergies = req.body.allergies;
			account.save();
			console.log(account.restrictions);
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders});
		}
	});
	
});

app.use('/search_account', (req, res) => {
	var matching_accounts = [];
	var query = req.body.query.toLowerCase(); 
	Account.find({}, function(err, accounts) {
		if (err) {
			console.log(err);
			console.log("Error while getting accounts");
		} else {
			accounts.map(account => {
				if (account.username.toLowerCase().includes(query) || query.includes(account.username.toLowerCase()) || account.firstname.toLowerCase().includes(query) || query.includes(account.firstname.toLowerCase()) || account.lastname.toLowerCase().includes(query) || query.includes(account.lastname.toLowerCase())) {
					matching_accounts.push(account.username + " " + account.firstname + " " + account.lastname);
					console.log(account.username);
				}
			})
			res.send(matching_accounts);
		}
	});
});

app.use('/get_account', (req, res) => {
	Account.findOne({username:req.body.username}, function(err, account) {
		if (err) {
			console.log(err);
			console.log("Account not found");
		} else {
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders});
		}
	});
});

app.use('/post_food', (req, res) => {
	console.log("reached");
	/*
	var GridFS = Grid(mongoose.connection.db, mongoose.mongo);

	if (req.body.picture) {
		var connection = mongoose.connection;
		connection.on('error', console.error.bind(console, 'connection error:'));
		//connection.once('open', function () {

		//var gfs = gridfs(connection.db);

	    // Writing a file from local to MongoDB
	    var writestream = GridFS.createWriteStream({ filename: req.body.picture });
	    fs.createReadStream(req.body.picturePath).pipe(writestream);
	    writestream.on('close', function (file) {
	        console.log("Picture file successfully saved");
	    });
	}
	*/
    var newFood = new Food ({
		id: req.body.id,
		quantity: req.body.quantity,
		foodName: req.body.foodName,
		sellerUserName: req.body.sellerUserName,
		description: req.body.description,
		ingredients: req.body.ingredients,
		restrictions: req.body.restrictions,
		cuisines: req.body.cuisines,
		picture: req.body.picture,
		picturePath: req.body.picturePath,
		isAvailable: req.body.isAvailable,
		location: req.body.location,
		postDate: req.body.postDate
	});

	newFood.save((err) => {
		if (err) {
			console.log("Error saving food to database");
			console.log(err);
			res.send(null);
		} else {
			console.log("Food saved correctly");
		}
	});
	//});
});


app.use('/get_foods', (req, res) => {
	Food.find({}, function (err, foods) {
		if (err) {
			console.log(err);
			console.log("Error with retrieving foods");
			res.json({});
		} else {
			var returnArr = [];

			console.log(foods);

			foods.map(food => {
				returnArr.push({"id":food.id, "quantity":food.quantity, "foodName":food.foodName,
					"sellerUserName":food.sellerUserName, "description":food.description, "ingredients":food.ingredients,
					"restrictions":food.restrictions, "cuisines":food.cuisines, "picture":food.picture, "picturePath":food.picturePath,
					"isAvailable":food.isAvailable, "location":food.location, "date":food.postDate});
			});

			res.json(returnArr);
		}
	});
});

app.use('/req_food', (req, res) => {
	console.log("reached REQFOOD");
	Food.findOne({id:req.body.id}, function (err, food) {
		if (err) {
			console.log(err);
			console.log("Error with retrieving food");
			res.json({});
		} else {
			console.log("Food returned");
			res.json({"id":food.id, "quantity":food.quantity, "foodName":food.foodName,
					"sellerUserName":food.sellerUserName, "description":food.description, "ingredients":food.ingredients,
					"restrictions":food.restrictions, "cuisines":food.cuisines, "picture":food.picture, "picturePath":food.picturePath,
					"isAvailable":food.isAvailable, "location":food.location, "date":food.postDate});
		}
	});
});

app.use('/order_food', (req, res) => {
	console.log("reached order food");
	Food.findOne({id:req.body.id}, function (err, food) {
		if (err) {
			console.log(err);
			console.log("Failed to order food");
			//res.json({});
		} else {
			console.log("Food ordered");
			food.isAvailable = false;
			food.save();
		}
	});
	Account.findOne({username:req.body.username}, function (err, account) {
		if (err) {
			console.log(err);
			console.log("Order account retrieval failed");
			res.json({});
		} else {
			console.log("Order Account found");
			account.orders.push(req.body.foodName);
			account.save();
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders})
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
