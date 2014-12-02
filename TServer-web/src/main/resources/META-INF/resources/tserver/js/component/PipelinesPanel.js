define([
    'jquery',
    'can',
    'text!js/template/PipelinesPanel.hbs',
    'js/model/Pipeline',
    'can/view/stache'
],function($,can,template,Pipeline){
    return can.Component.extend({
        tag: 'pipelines-panel',
        template: can.stache(template),
        init: function(element,parent){
            var that = this;
            $(element).attr('id',this.id);
            var panels = parent.scope.attr('panels');
            if (panels) {
                panels.splice(0,0,this);
            } else {
                panels = new can.List([this]);
                parent.scope.attr('panels',panels);
            }

            Pipeline.findAll().done(function(res){
                that.scope.attr('pipelines',res);
            });
        },
        scope: {
            id: 'pipelinesPanel',
            name: 'Pipelines',
            active: false,
            toggle: function(value){
                if (typeof value!=='undefined'){
                    this.attr('active',value);
                } else {
                    this.attr('active',!this.attr('active'));
                }
            }
        }
    });
});