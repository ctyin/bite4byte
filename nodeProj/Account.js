var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/myDatabase');

var Schema = mongoose.Schema;

var accountSchema = new Schema({
	username: {type: String, required: true, unique: true},
	lastname: {type: String, required: true, unique: false},
	firstname: {type: String, required: true, unique: false},
	password: {type: String, required: true, unique: false}
    });

// export personSchema as a class called Person
module.exports = mongoose.model('Account', accountSchema);

/*accountSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}*/
