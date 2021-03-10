
var app = new Vue({
  el: '#main',
  data: {
  	isReady: false,
  	data: {active: 0, list: []}
  },
  computed: {
  	formIsEmpty: function() {
  		let self = this;
  		let idx = Object.keys(this.data.list[this.data.active].form).findIndex(function(el) {
  			return !self.isEmpty(self.data.list[self.data.active].form[el]);
  		});
  		return !~idx ? true : false;
  	}
  },
  methods: {
  	fillForm: function(row) {
  		let self = this;
  		Object.keys(this.data.list[this.data.active].form).forEach(function(el) {
  			self.data.list[self.data.active].form[el] = row[el];
  		});
  		this.data.list[this.data.active].inEdit = true;
  		this.data.list[this.data.active].editedRow = row[this.data.list[this.data.active].columns[0]];
  	},
  	clearForm: function() {
  		let self = this;
  		Object.keys(this.data.list[this.data.active].form).forEach(function(el) {
  			self.data.list[self.data.active].form[el] = null;
  		});
  		this.data.list[this.data.active].inEdit = false;
  	},
  	insertRow: function() {
  		let obj = {
  			id: [this.data.list[this.data.active].columns[0]],
  			row: this.data.list[this.data.active].form
  		};
  		this.processRow(obj);
  	},
  	updateRow: function() {
  		let obj = {
  			row: this.data.list[this.data.active].form,
  			id: [this.data.list[this.data.active].columns[0], this.data.list[this.data.active].editedRow]
  		};
  		this.processRow(obj);
  	},
  	deleteRow: function(id) {
  		let obj = {
  			id: [this.data.list[this.data.active].columns[0], id]
  		};
  		this.processRow(obj);
  	},
  	processRow: function(val) {
  		let self = this;
  		let obj = {title: this.data.list[this.data.active].title};
  		Object.keys(val).forEach(function(el) {
  			obj[el] = JSON.stringify(val[el]);
  		});
  		$.post('data', obj).then(function(data) {
  			if (data.success) {
  				let index = self.data.list[self.data.active].columns[0];

  				if (!val.row) {
  					self.data.list[self.data.active].list = self.data.list[self.data.active].list.filter(function(el) {
	  					return el[index] != val.id[1];
	  				});
  				} else if (val.id.length < 2) {
  					let row = {};
  					row[index] = data.data;
  					Object.keys(val.row).forEach(function(el) {
  						row[el] = self.data.list[self.data.active].form[el].toString();
  						self.data.list[self.data.active].form[el] = null;
  					});
  					self.data.list[self.data.active].list.push(row);
  				} else {
  					let row = self.data.list[self.data.active].list.findIndex(function(el) {
  						return el[index] == val.id[1];
  					});
  					Object.keys(val.row).forEach(function(el) {
  						self.data.list[self.data.active].list[row][el] = self.data.list[self.data.active].form[el].toString();
  						self.data.list[self.data.active].form[el] = null;
  					});
  				}

  			} else {
  				toast(data.data, 'black', 'orange');
  			}
  		}).catch(function(error) {
  			let err = error.statusText ? error.statusText : error;
  			toast(err, 'red', 'yellow');
  		});

  	},
  	isEmpty: function(val) {
  		return val == null || !val.trim().length ? true : false;
  	},
  	numberByInt: function(val) {
  		if (parseInt(val) && Math.abs(val) < 9) {
  			let numbers = {1: 'one', 2: 'two', 3: 'three', 4: 'four',
  										 5: 'five', 6: 'six', 7: 'seven', 8: 'eight'};
  			return numbers[Math.abs(val)];
  		}
  		return null;
  	}
  },
  created: function() {
  	let self = this;
  	$.post('./init', {}).then(function(data) {
  		if (data.success) {
  			data.data.forEach(function(tbl) {
	  			tbl.form = {};
	  			tbl.inEdit = false;
	  			tbl.columns.forEach(function(clm, key) {
	  				if (key > 0) {
	  					tbl.form[clm] = null;
	  				}
	  			});
	  		});
  			self.data.list = data.data;

  			self.isReady = true;
  		} else {
  			toast(data.data, 'black', 'orange');
  		}
  	}).catch(function(error) {
  		let err = error.statusText ? error.statusText : error;
  		toast(err, 'red', 'yellow');
  	});
  }
});


var toast = function(title, color, progressColor, duration) {

	let sDuration = duration ? duration : 3000;
	let sColor = color ? color : 'white';
	let sTitle = title ? title : ':(';
	let sProgressColor = progressColor ? progressColor : 'red';

	$('body').toast({
		class: sColor,
	    message: sTitle,
	    showProgress: 'bottom',
	    displayTime: sDuration,
	    classProgress: sProgressColor
	 });
}