---
layout: default
title: Jobs
has_children: true
---

# Jobs

Jobs provide a way to make money while performing common actions such as mining ore and chopping down trees. Custom jobs can be created or existing jobs modified to fit your needs.

## Concepts

### Job Reward

A job reward determines how much experience and money a player gets for performing certain actions against objects (i.e. breaking blocks, killing mobs, smelting, etc.).

### Job Action

Each job reward must have a single action associated with it. They describe the action a user must take in order to gain the reward. Currently, the following actions are implemented:

- `break`: action for breaking blocks
- `place`: action for placing blocks
- `kill`: action for killing entities
- `fish`: action for catching fish and other items through fishing