var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');
var Schema = mongoose.Schema;

var groupSchema = new Schema({
	name: {type: String, required: true, unique: true},
	users: {type: String, required: true, unique: false},
	posts: {type: String, required: true, unique: false},
});

module.exports = mongoose.model('Group', groupSchema);