define([
    'jquery',
    'can',
    'text!template/TSInput.hbs',
    'can/view/stache'
],function($,can,template){
    return can.Component.extend({
        init: function(element,parent){
          console.log('ts-input init', arguments);
        },
        tag: 'ts-input',
        template: can.stache(template),
        scope:{
        },
        events: {
            'input change': function(element, event){
                this.scope.attr('value',element.val());
            },
            'input keyup': function(element, event){
                this.scope.attr('value',element.val());
            },
            'input cut': function(element, event){
                this.scope.attr('value',element.val());
            },
            'input paste': function(element, event){
                this.scope.attr('value',element.val());
            }
        }
    });
});