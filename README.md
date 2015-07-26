# Betfair

## Configuration file example

```json
{
    "login": {
        "username": "USERNAME",
        "password": "PASSWORD",
        "appKey": "ABC123"
    },
    "monitors": [
        {
            "enabled": true,
            "eventType": "1",
            "inPlay": true,
            "marketTypes": [
                "OVER_UNDER_15",
                "MATCH_ODDS"
            ]
        },
        {
            "enabled": false,
            "eventType": "2",
            "inPlay": false,
            "marketTypes": [
                "MATCH_ODDS"
            ]
        }
    ]
}
```