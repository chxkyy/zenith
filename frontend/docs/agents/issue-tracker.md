# Issue Tracker: Local Markdown

Issues for this repo are stored as **local markdown files** under `.scratch/<feature>/`.

## Convention

- Each feature or work item gets a directory under `.scratch/`
- Within that directory, issues are written as `.md` files
- The `to-issues` skill should create new issue files here
- The `triage` and `qa` skills should read from and update these files

## Workflow

1. When `to-issues` breaks down a plan, it creates `.scratch/<feature>/issue-NN-description.md`
2. When `triage` processes an issue, it updates the file's frontmatter or headings to reflect the new status
3. Labels are represented as tags in the markdown (e.g., `Status: needs-triage`)

## CLI Commands

No remote CLI (`gh`/`glab`) is needed. All operations are local file reads/writes.
