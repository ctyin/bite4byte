var mongoose = require('mongoose');

mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var foodSchema = new Schema({
	id: {type: String, required:true, unique:true},
	quantity: {type: Number, required:true, unique:false},
	foodName: {type: String, required:true, unique:false},
	sellerUserName: {type: String, required:true, unique:false},
	description: {type:String, required:true, unique:false},
	ingredients: {type:[String], required:true, unique:false},
	restrictions: {type:[String], required:true, unique:false},
	cuisines: {type:[String], required:true, unique:false},
	picture: {type:String, required:false, unique:true},
	picturePath: {type:String, required:false, unique:true},
	isAvailable: {type:Boolean, required:true, unique:false},
	location: {type:String, required:true, unique:false},
	postDate: {type: Date, required:true, unique:false}
});

module.exports = mongoose.model('Food', foodSchema);