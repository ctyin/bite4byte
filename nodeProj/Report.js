var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');
var Schema = mongoose.Schema;

var reportSchema = new Schema({
	filingUser: {type: String, required: true, unique: false},
	reportedUser: {type: String, required: true, unique: false},
	reason: {type: String, required: true, unique: false}
});

module.exports = mongoose.model('Report', reportSchema);