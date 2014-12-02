define([
    'jquery',
    'can',
    'text!js/template/ContextsPanel.hbs',
    'can/view/stache'
],function($,can,template){
    return can.Component.extend({
        tag: 'contexts-panel',
        template: can.stache(template),
        init: function(element,parent){
            $(element).attr('id',this.id);

            var panels = parent.scope.attr('panels');
            if (panels) {
                panels.splice(0,0,this);
            } else {
                panels = new can.List([this]);
                parent.scope.attr('panels',panels);
            }
        },
        scope: {
            id: 'contextsPanel',
            name: 'Contexts',
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