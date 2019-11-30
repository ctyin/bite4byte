var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var accountSchema = new Schema({
	username: {type: String, required: true, unique: true},
	lastname: {type: String, required: true, unique: false},
	firstname: {type: String, required: true, unique: false},
	password: {type: String, required: true, unique: false},
	restrictions: {type: [String], required: false, unique: false},
	allergies: {type: [String], required: false, unique: false},
	orders: {type: [String], required: false, unique: false},
	rating: {type: Number, required: false, unique: false},
	numRatedBy: {type: Number, required: false, unique: false}
    });

// export personSchema as a class called Person
module.exports = mongoose.model('Account', accountSchema);

/*accountSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}*/
