---
layout: default
title: Commands
nav_order: 4
---

# Commands

Parameters in `<angle brackets>` are required and parameters in `[square brackets]` are optional.

| Command                             | Description                                                                  | Permission             |
|-------------------------------------|------------------------------------------------------------------------------|------------------------|
| `/balance [currency]`               | Show your balance. Uses the default currency unless you name another one.    | `totaleconomy.balance` |
| `/pay <player> <amount> [currency]` | Pay an online player. Uses the default currency unless you name another one. | `totaleconomy.pay`     |

## Examples

```
/balance
/balance COIN
/pay Steve 25
/pay Steve 2.50 USD
```

## Notes

- `currency` is the currency's code, such as `USD`. Press Tab after `/balance` to see the available codes.
- The smallest amount you can pay is `0.01`.
- You can't pay yourself, and you can't pay more than your balance.
