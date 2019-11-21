var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');
var Schema = mongoose.Schema;

var conversationSchema = new Schema({
	convo_id: {type: String, required: true, unique: true},
	participants: [String]
});

module.exports = mongoose.model('Convo', conversationSchema);