<sub>[back](./SQL Proposal.md)</sub>

## Configuration file changes

### Jobs

Simplest first. Jobs are now identified by a ``UUID`` which means the job nodes changes slightly.

#### Old

```hocon
"job-name" {
	"level"=0
	...
}
```

#### New

```hocon
"job-uuid" {
  "level"=0
  "displayname"="job-name"
  ...
}
```

For completeness I did not omit the ``"``s in this example.

These changes will automatically be converted when the configuration is loaded. This will be performed for any job that doesn't match the ``UUID_PATTERN`` which means that creating a new job according to the old schema will be a perfectly valid way to go. (The job will be assigned an ID upon the next configuration load)

__Note__: The requirements currently still use the names to identify required jobs.

---

### Accounts

The same strategy with the ``UUID``s has been applied to virtual accounts. The same conversion description applies here.

Also to make the configuration match the database configuration a little more I altered the balance configuration slightly.

Basically the schema changed like this:

#### Old

```hocon
"96f61bf0-eb5d-4859-8a5d-6af937f790f4" {
    dollar-balance="101.75"
    ...
}
```

#### New

```hocon
"96f61bf0-eb5d-4859-8a5d-6af937f790f4" {
    balance {
        dollar="101.75"
    }
    "displayname"="Displayname for virtual accounts"
    ...
}
```

This is also automatically converted when the configuration is loaded.