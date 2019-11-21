var mongoose = require('mongoose');
require('mongoose-long')(mongoose);
var SchemaTypes = mongoose.Schema.Types;

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/myDatabase');
var Schema = mongoose.Schema;

var messageSchema = new Schema({
	sender: {type: String, required:true},
	convo_id: {type: String, required: true, unique: false},
	contents: {type: String},
	created_at: {type: SchemaTypes.Long}
});

module.exports = mongoose.model('message', messageSchema);