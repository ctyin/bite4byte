var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/myDatabase');
var Schema = mongoose.Schema;

var messageSchema = new Schema({
	sender: {type: String, required:true},
	convo_id: {type: String, required: true, unique: true},
	contents: {type: String}
});

module.exports = mongoose.model('message', messageSchema);