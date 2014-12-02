define([
    'jquery',
    'can'
],function($,can) {
    var Pipeline = can.Model.extend({
        id: 'name',
        resource: require.toUrl('pipelines'),
        findAll: 'GET '+require.toUrl('pipelines'),
        findOne: 'GET '+require.toUrl('pipelines/{name}'),
        update: 'PUT '+require.toUrl('pipelines/{name}')
    });
    return Pipeline;
});