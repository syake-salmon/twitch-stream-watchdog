const PROPERTY_KEY_TWITCH_MY_USER_ID: string = 'TWITCH_MY_USER_ID';
const PROPERTY_KEY_TWITCH_TOKEN: string = 'TWITCH_TOKEN';
const PROPERTY_KEY_TWITCH_CLIENT_ID: string = 'TWITCH_CLIENT_ID';
const PROPERTY_KEY_CALLBACK_URL: string = 'CALLBACK_URL';
const PROPERTY_KEY_SLACK_WEBHOOK_ENDPOINT: string = 'SLACK_WEBHOOK_ENDPOINT';

var properties: GoogleAppsScript.Properties.Properties = PropertiesService.getScriptProperties();

function _daily(): void {
    console.time('----- _daily -----');

    try {
        var token: string = properties.getProperty(PROPERTY_KEY_TWITCH_TOKEN);
        var clientId: string = properties.getProperty(PROPERTY_KEY_TWITCH_CLIENT_ID);
        var userId: string = properties.getProperty(PROPERTY_KEY_TWITCH_MY_USER_ID);

        var users: TwitchUser[] = [];
        users = users.concat(getFollows(token, clientId, userId));
        users = users.concat(getUsers(token, clientId, [userId]));

        requestSubscriptionWebhook(token, clientId, users);
    } catch (e) {
        console.error(e);

        var options: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions = {
            method: 'post',
            payload: JSON.stringify({ 'username': 'twitch-webhook-subscriber', 'text': '日次処理中にエラーが発生しました。<https://script.google.com/home/projects/1jdHjoUbpoKPCByjt71PSb3OxD7XcRIr86WwGcGvxB_6zjRCWWT9fLBXR/executions|[ログ]>\nERROR=>' + e.message }),
            muteHttpExceptions: true
        };
        callExternalAPI(properties.getProperty(PROPERTY_KEY_SLACK_WEBHOOK_ENDPOINT), options);
    }

    console.timeEnd('----- _daily -----');
}

function getFollows(token: string, clientId: string, userId: string): TwitchUser[] {
    console.time('----- getFollows -----');

    var options: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions = {
        method: 'get',
        contentType: 'application/json',
        headers: {
            Authorization: 'Bearer ' + token,
            'client-id': clientId
        },
        muteHttpExceptions: true
    };
    var response: GoogleAppsScript.URL_Fetch.HTTPResponse = callExternalAPI('https://api.twitch.tv/helix/users/follows?first=100&from_id=' + userId, options);
    var follows: any[] = JSON.parse(response.getContentText()).data;
    var users: TwitchUser[] = [];
    for (var i: number = 0; i < follows.length; i++) {
        users.push(new TwitchUser(follows[i].to_id, follows[i].to_name));
    }

    console.timeEnd('----- getFollows -----');
    return users;
}

function getUsers(token: string, clientId: string, userIds: string[]): TwitchUser[] {
    console.time('----- getUserInfo -----');

    var options: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions = {
        method: 'get',
        contentType: 'application/json',
        headers: {
            Authorization: 'Bearer ' + token,
            'client-id': clientId
        },
        muteHttpExceptions: true
    };

    var endpoint: string = 'https://api.twitch.tv/helix/users?id=';
    for (var i: number = 0; i < userIds.length; i++) {
        endpoint = endpoint + userIds[i] + '&';
    }
    endpoint.slice(0, -1);

    var response: GoogleAppsScript.URL_Fetch.HTTPResponse = callExternalAPI(endpoint, options);
    var data: any[] = JSON.parse(response.getContentText()).data;

    var users: TwitchUser[] = [];
    for (var i: number = 0; i < data.length; i++) {
        users.push(new TwitchUser(data[i].id, data[i].display_name));
    }

    console.timeEnd('----- getUserInfo -----');
    return users;
}

function requestSubscriptionWebhook(token: string, clientId: string, target: TwitchUser[]): void {
    console.time('----- requestSubscriptionWebhook -----');

    for (var i: number = 0; i < target.length; i++) {
        var options: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions = {
            method: 'post',
            headers: {
                Authorization: 'Bearer ' + token,
                'client-id': clientId
            },
            payload: {
                'hub.callback': properties.getProperty(PROPERTY_KEY_CALLBACK_URL),
                'hub.mode': 'subscribe',
                'hub.topic': 'https://api.twitch.tv/helix/streams?user_id=' + target[i].id,
                'hub.lease_seconds': '864000'
            },
            muteHttpExceptions: true
        };
        var response: GoogleAppsScript.URL_Fetch.HTTPResponse = callExternalAPI('https://api.twitch.tv/helix/webhooks/hub', options);
        var status: string = String(response.getResponseCode());
        if (status.charAt(0) == '2') {
            console.log('Succeeded to subscribe webhook. user=[%s]', target[i].name);
        } else {
            console.log('Failed to subscribe webhook. user=[%s] status=[%s] message=[%s]', target[i].name, status, response.getContentText())
        }
    }

    console.timeEnd('----- requestSubscriptionWebhook -----');
}

function callExternalAPI(endpoint: string, options: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions): GoogleAppsScript.URL_Fetch.HTTPResponse {
    console.time('----- callExternalAPI -----');

    var response: GoogleAppsScript.URL_Fetch.HTTPResponse = UrlFetchApp.fetch(endpoint, options);

    console.timeEnd('----- callExternalAPI -----');
    return response;
}

class TwitchUser {
    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

    id: string;
    name: string;
}