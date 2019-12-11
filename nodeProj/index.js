// set up Express
const uuidv4 = require('uuid/v4');
var express = require('express');
var session = require('express-session');
var app = express();
var fs = require('fs');
var mongoose = require("mongoose");
var Grid = require('gridfs-stream');
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

// Redis for session store
// const redis = require('redis');
// const redisStore = require('connect-redis')(session);
// const client  = redis.createClient();
// const router = express.Router();

// Start using the express-session code
app.use(session({
    secret: 'TY@[$xs2+2~59pkVVAaS!jRf',
    // create new redis store.
    // store: new redisStore({ host: 'localhost', port: 6379, client: client,ttl : 260}),
    saveUninitialized: true,
    resave: true
}));

var sess = null;

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Account class from Account.js
var Account = require('./Account.js');
var Convo = require('./Convo.js');
var Message = require('./Message');
var Report = require('./Report');
var Food = require('./Food.js');
var Group = require('./Group.js')
var Admin = require('./Admin.js');

/***************************************/

// need the http server instance for socket.io
var server = app.listen(3000,  () => {
	console.log('Listening on port 3000');
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
				res.json({"username":account.username, "password":account.password, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames": account.groupNames, "banned": account.banned});
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

	Group.find({}, function (groups, err) {
		if (err) {
			console.log(err);
			console.log("Error getting all groups while deleting account");
		} else {
			groups.map(group => {
				if (group.users.includes(username)) {
					group.users.pull(username);
					group.save();
				}
			});
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

	Convo.find({}, function(err, convos) {
		if (err) {
			console.log(err);
			console.log("Error getting all convos.");
		} else {
			var toRemove = [];
			convos.map(convo => {
				if (convo.participants.includes(username)) {
					toRemove.push(convo.convo_id);
				}
				toRemove.forEach(function (convo_id) {
					Convo.remove({convo_id : convo_id}, function(err) {
						if (err) {
							console.log(err);
							console.log("Error removing convo with convo_id " + convo_id);
						} else {
							console.log("Convo removed with id " + convo_id);
						}
					});
				});
			});
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
			orders: req.body.orders,
			rating: req.body.rating,
			numRatedBy: req.body.numRatedBy,
			friends: req.body.friends,
			friend_requests: req.body.friendRequests,
			groupNames: req.body.groupNames,
			banned: false
	    });

	newAccount.save(function (err) {
		if (err) {
			console.log("Error saving new account to database");
			console.log(err);
			res.json({});
		} else {
			console.log("Account saved correctly");
			res.json({"username": newAccount.username, "firstname": newAccount.firstname, "lastname": newAccount.lastname, "restrictions": newAccount.restrictions, "allergies": newAccount.allergies, "orders":newAccount.orders, "rating":newAccount.rating, "numRatedBy":newAccount.numRatedBy, "friends":newAccount.friends, "friend_requests":newAccount.friend_requests, "groupNames":newAccount.groupNames, "banned": newAccount.banned});
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
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
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
			});
			res.send(matching_accounts);
		}
	});
});

app.use('/get_user_rating', (req, res) => {
	username = req.body.username;
	Account.findOne({username:username}, function(err, account) {
		if (err) {
			console.log(err);
			console.log("Error while getting rating");
		} else {
			console.log("User Rating found: " + account.rating);
			res.send(account.rating);
		}
	});
});

app.use('/update_user_rating', (req, res) => {
	username = req.body.username;
	newRating = parseFloat(req.body.rating);
	Account.findOne({username:username}, function(err, account) {
		if (err) {
			console.log(err);
			console.log("Error while updating rating");
		} else {
			updatedCount = account.numRatedBy + 1;
			updatedRating = account.rating * account.numRatedBy;
			console.log(updatedRating);
			updatedRating = updatedRating + newRating;
			console.log(updatedRating);
			updatedRating = updatedRating / updatedCount;
			console.log(updatedRating);
			account.numRatedBy = updatedCount;
			account.rating = updatedRating;
			account.save();
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
			console.log("Updated numRatedBy: " + account.numRatedBy + " Updated rating: " + account.rating);
		}
	});
});

app.use('/get_account', (req, res) => {
	Account.findOne({username:req.body.username}, function(err, account) {
		if (err) {
			console.log(err);
			console.log("Account not found");
		} else {
			console.log(account.username);
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
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
		postDate: req.body.postDate,
		group: req.body.groupBool
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
					"isAvailable":food.isAvailable, "location":food.location, "date":food.postDate, "group":food.group});
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
					"isAvailable":food.isAvailable, "location":food.location, "date":food.postDate, "group":food.group});
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
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
		}
	});
});

app.use('/friend_request', (req, res) => {
	Account.findOne({username:req.body.recipient}, function (err, account) {
		if (err) {
			console.log(err);
			console.log("Friend Request Failed");
			res.json({});
		} else {
			if (account.friend_requests.includes(req.body.sender)) {
				console.log("Friend request already made");
			} else {
				console.log("Friend request made");
				account.friend_requests.push(req.body.sender);
				account.save();
			}
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
		}
	});
});

app.use('/accept_friend_request', (req, res) => {
	Account.findOne({username:req.body.sender}, function (err, account) {
		if (err) {
			console.log(err);
			console.log("Acceptor could not accept");
			res.json({});
		} else {
			account.friends.push(req.body.acceptor);
			account.save();
		}
	});
	Account.findOne({username:req.body.acceptor}, function (err, account) { 
		if (err) {
			console.log(err);
			console.log("Sender could not accept");
			res.json({});
		} else {
			account.friends.push(req.body.sender);
			account.friend_requests.pull(req.body.sender);
			account.save();
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
		}
	});
});

app.use('/decline_friend_request', (req, res) => {
	Account.findOne({username:req.body.decliner}, function (err, account) {
		if (err) {
			console.log(err);
			console.log("Could not decline");
			res.json({});
		} else {
			account.friend_requests.pull(req.body.sender);
			account.save();
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames, "banned": account.banned});
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

	Convo.find( queryObj, (err, convos) => {
    	if (err) {
    		console.log(err);
    		res.json({});
    	}
    	else {
    		var returnArray = [];
    		convos.forEach((convo) => {
    			returnArray.push( {"convo_id" : convo.convo_id, "participants" : convo.participants});
    		});

    		res.json(returnArray);
    	}
    });
});

app.use('/singleconvo', (req, res) => {
	var queryObj = {};
	if (req.body.convo_id) {
		queryObj = {"convo_id" : req.body.convo_id};
	}

	Message.find(queryObj).sort({"created_at": 1}).exec((err, messages) => {
		if (err) {
			console.log(err);
			res.json({});
		} else {
			var returnArray = [];
			messages.forEach((msg) => {
				returnArray.push({"convo_id" : msg.convo_id, "sender" : msg.sender, "contents" : msg.contents, "created_at" : msg.created_at});
			});

			res.json(returnArray);
		}
	});
});

app.use('/saveMessage', (req, res) => {
	var newMsg = new Message({
		sender: req.body.sender,
		convo_id: req.body.convo_id,
		contents: req.body.contents,
		created_at: req.body.created_at
	});

	newMsg.save((err) => {
		if (err) {
			console.log(err);
			res.json({});
		} else {
			console.log("Saved message correctly");
		}
	})
});

app.use('/startConvo', (req, res) => {
	var gen_id = uuidv4();
	var receiver = req.body.receiver;
	var sender = req.body.currUser;
	var returnArray = []

	Account.findOne({username:receiver}, (err, account) => {
		if (err || account == null) {
			console.log("Starting a convo: username that doesn't exist");
			res.send([]);
			return;
		} else {
			// receiver name is valid

			// make sure we're not creating a duplicate 
			Convo.findOne({$and: [{"participants": sender}, {"participants": receiver}]}, (err, convo) => {
				if (err || convo != null) {
					console.log(convo.convo_id);
					console.log("Starting a convo: conversation already exists")
					res.send([]);
				} else {
					var newConvo = new Convo({
						convo_id: gen_id,
						participants: [receiver, sender]
					});

					newConvo.save((err) => {
						if (err) {
							console.log(err);
							res.send([]);
						} else {
							console.log("Created new conversation!");
							returnArray.push(gen_id);
							res.send(returnArray);
						}
					});
				}
			});
		}
	});
});

app.use('/fileReport', (req, res) => {

	var newReport = new Report ({
			filingUser: req.body.filingUser,
			reportedUser: req.body.reportedUser,
			reason: req.body.reason
    });

	newReport.save(function (err) {
		if (err) {
			console.log("Error saving new report to database");
			console.log(err);
			res.json({});
		} else {
			console.log("Report filed correctly");
			res.json("Your report against " + newReport.reportedUser + " has been filed and will be reviewed.");
		}
	});
});

app.use('/createGroup', (req, res) => {
	var creator = req.body.creator;
	var tgt;
	
	Group.findOne({name: req.body.name}, function (err, group) {
		if (err) {
			console.log("Create group, unique group name validation error");
			res.json({});
		}
		if (group != null) {
			res.json({});
		} else {
			var newGroup = new Group({
				name: req.body.name,
				users: req.body.users,
				posts: req.body.posts
			});
			newGroup.save(function (err) {
				if (err) {
					console.log("Error saving group");
					res.json({});
				} else {
					console.log("Group saved");
					
					newGroup.users.forEach(function (username) {
						Account.findOne({username: username}, function (err, account) {
							if (err) {
								console.log("Error adding group to user");
								res.json({});
							} else {
								account.groupNames.push(newGroup.name);
								account.save((err) => {
									if (err) {
										console.log("Didn't save successfully");
									} else {
										console.log("Group name " + newGroup.name + " added to account " + account.username);
										if (account.username == creator) {
											Account.findOne({username: creator}, function (err, creator) {
												if (err) {
													console.log("Error getting creator");
													res.json({});
												} else {
													res.json({"username":creator.username, "firstname":creator.firstname, "lastname":creator.lastname, "restrictions":creator.restrictions, "allergies":creator.allergies, "orders":creator.orders, "rating":creator.rating, "numRatedBy":creator.numRatedBy, "friends":creator.friends, "friend_requests":creator.friend_requests, "groupNames":creator.groupNames, "banned": creator.banned});
												}
											});
										}
>>>>>>> f49cc8626fa2d976771a2152ea1241c374139a0d
									}
								});
							}
						});
					});
				}
			});

		}
	});
});

app.use('/postToGroup', (req, res) => {
	Group.findOne({name: req.body.groupName}, function (err, group) {
		if (err) {
			console.log("Post to group, group not found");
			res.json({});
		} else {
			group.posts.push(req.body.id);
			group.save();
			console.log("Post added to group");
		}
	});

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
		postDate: req.body.postDate,
		group: req.body.groupBool
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

	res.json({});
});

app.use('/getGroup', (req, res) => {
	Group.findOne({name: req.body.name}, function (err, group) {
		if (err) {
			console.log("Error getting group");
			res.json({});
		} else {
			res.json({"name": group.name, "users": group.users, "posts": group.posts});
		}
	});
});

/*app.use('/leaveGroup', (req, res) => {
	var username = req.body.username;
	var groupName = req.body.groupName;
	Group.findOne({name : groupName}, function (err, group) {
		if (err) {
			console.log(err);
			console.log("Could not find group");
		} else {
			group.users.pull(username);
		}
	});
	Account.findOne({username : username}, function (err, account) {
		if (err) {
			console.log(err);
			console.log("Could not find account");
		} else {
			account.groupNames.pull(groupName);
			res.json({"username":account.username, "firstname":account.firstname, "lastname":account.lastname, "restrictions":account.restrictions, "allergies":account.allergies, "orders":account.orders, "rating":account.rating, "numRatedBy":account.numRatedBy, "friends":account.friends, "friend_requests":account.friend_requests, "groupNames":account.groupNames});
		}
	});

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

app.get(['/', '/login'], (req, res) => {
	sess = req.session;
	if (sess.username) {
		return res.redirect('/home');
	}

	res.render('./pages/login', {title: "Login - Bite 4 Byte", invalid: 0});
});

app.post('/loginAdmin', (req, res) => {
	sess = req.session;
	var name = req.body.username;
	var password = req.body.password;
	Admin.findOne({username: name}, (err, account) => {
		if (err || account == null) {		//Account doesn't exist
			res.render('./pages/login', {title:"Login - Bite 4 Byte", invalid: 1});
			console.log("Invalid Username");
		} else {
			if (password == account.password) { //Account exists and pswd matches
				sess.username = name;
				res.render("./pages/index", {title:"Bite 4 Byte"});
				console.log("Welcome Back!");
			} else {
				res.render('./pages/login', {title:"Login - Bite 4 Byte", invalid: 1});			//Account exists but incorrect pswd
				console.log("Incorrect password");
			}
		}
	});
});

app.get('/createAccount', (req, res) => {
	res.render('./pages/createAccount', {title: "Create account", invalid:0});
});

app.post('/createAccount', (req, res) => {
	var name = req.body.username;
	sess = req.session;

	if (req.body.password !== req.body.password2) {
		// passwords don't match
		res.render('./pages/createAccount', {title: "Create account", invalid:1});
	} else {
		Admin.findOne({username: name}, (err, account) => {
			if (err || account !== null) {
				// admin username already exists
				res.render('./pages/createAccount', {title: "Create account", invalid:2});
			} else {
				// create the session
				sess.username = name;

				// want to save the new admin then redirect
				var newAdmin = new Admin ({
						username: req.body.username,
						firstname: req.body.firstname,
						lastname: req.body.lastname,
						password: req.body.password
			    });

			    newAdmin.save((err) => {
			    	if (err) {
			    		console.log("Error saving new admin to database");
			    		console.log(err);
			    		res.render('./pages/createAccount', {title: "Create account", invalid:3});
			    	} else {
			    		console.log("New admin saved correctly");
			    		res.redirect('/home');
			    	}	
			    })
			}
		});
	}

})

app.use('/account', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Account.find({}, (err, accounts) => {
			res.render('./pages/account', {accounts: accounts, title: "Account Data"});
		});
	} else {
		res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
	}
});

app.get('/deleteAccount/:id', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Account.deleteOne({_id:req.params.id}, (err, result) => {
			if (err) {
				console.log(err);
			} else {
				res.redirect('/account');
			}
		});	
	} else {
		res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
	}
});

app.get('/unbanAccount/:id', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Account.findOne({_id:req.params.id}, (err, account) => {
			if (err) {
				console.log(err);
			} else {
				account.banned = false;
				account.save();
				res.redirect('/account');
			}
		});
	}
});

app.get('/banAccount/:id', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Account.findOne({_id:req.params.id}, (err, account) => {
			if (err) {
				console.log(err);
			} else {
				account.banned = true;
				account.save();
				Food.deleteMany({sellerUserName:account.username}, (err, result) => {
					if (err) {
						console.log(err);
					} else {
						console.log("Food Deleted");
					}
				});
				res.redirect('/account');
			}
		});
	}
});

app.use('/food', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Food.find({}, (err, foods) => {
			res.render('./pages/food', {foods: foods, title: "Food Data"});
		});
	} else {
		res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
	}
});

app.get('/deleteFood/:id', (req, res) => {
	sess = req.session;
	if (sess.username) {
		Food.deleteOne({_id:req.params.id}, (err, result) => {
			if (err) {
				console.log(err);
			} else {
				res.redirect('/food');
			}
		});
	} else {
		res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
	}
});

app.use('/home', (req, res) => {
	sess = req.session;
	if (sess.username) {
		res.render('./pages/index', {title: "Bite 4 Byte"});
	} else {
		res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
	}
});

app.get('/logout', (req,res) => {
    req.session.destroy((err) => {
        if (err) {
            return console.log(err);
        }
        res.redirect('/');
    });
});
