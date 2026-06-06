# Triage Labels

This repo uses the **default triage label vocabulary**.

## Label Mapping

| Role | Label | Description |
|------|-------|-------------|
| needs-triage | `needs-triage` | Maintainer needs to evaluate |
| needs-info | `needs-info` | Waiting on reporter for more information |
| ready-for-agent | `ready-for-agent` | Fully specified, AFK-ready (agent can pick up with no human context) |
| ready-for-human | `ready-for-human` | Needs human implementation |
| wontfix | `wontfix` | Will not be actioned |

## Usage

- The `triage` skill applies these labels when processing incoming issues
- For local markdown issues, labels are stored as frontmatter or inline tags (e.g., `Labels: needs-triage`)
- If the issue tracker supports native labels, ensure these exact strings exist there
