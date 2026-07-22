# AGENTS.md

These instructions apply to the entire repository. Follow them for every task.

## Sources of truth and two-level planning

Use two mandatory and complementary levels of planning:

1. **Project plan:** [`.docs/project-plan.md`](.docs/project-plan.md) defines the product requirements, implementation sequence, scope, and recorded technical decisions. Read the relevant sections before planning or implementing any project change.
2. **Dialog plan:** before editing files, prepare a task-specific plan in the conversation with the user. It must translate the relevant project-plan item into small, concrete, reviewable batches.

The dialog plan does not replace the project plan, and the project plan does not replace the dialog plan or the approval gate. Both must be followed.

- Identify the relevant section, implementation stage, and checklist item from `.docs/project-plan.md` in the dialog plan.
- Ensure every proposed change is consistent with the requirements and decisions recorded in `.docs/project-plan.md`.
- Follow the implementation order from the project plan unless the user explicitly approves a justified deviation.
- Do not silently reinterpret, ignore, or contradict the project plan.
- If a request conflicts with the project plan, is not covered by it, or materially expands its scope, explain the discrepancy and wait for the user's decision before proposing implementation.
- Changes to `.docs/project-plan.md`, including checklist status updates, require their own explicitly approved batch.

## Core workflow: dialog plan, approval, one small batch, review

1. Inspect the relevant code, repository state, and applicable parts of `.docs/project-plan.md` using read-only operations.
2. Before changing any project file, present a concrete implementation plan. The plan must describe:
   - the applicable project-plan section, stage, and checklist item;
   - the intended result;
   - the files expected to change;
   - the proposed batches and the purpose of each batch;
   - the validation to run;
   - known risks, assumptions, and open decisions.
3. Wait for the user's explicit approval of the plan or of a specific batch.
4. Implement only one approved batch.
5. Validate that batch, summarize the exact changes and validation results, and stop for user review.
6. Continue with the next batch only after a new explicit user approval.

Never skip the approval gate. A request to investigate, fix, implement, refactor, or improve something is not by itself approval to edit files. Approval must be given after the plan or batch proposal is shown. Examples of explicit approval include "approve the plan", "implement batch 1", "go ahead with this batch", and equivalent unambiguous wording.

Approval is scoped to the plan and batch that the user approved. Do not treat it as permission for follow-up fixes, cleanup, extra tests, formatting, dependency updates, or additional batches.

## What is allowed before approval

Before approval, only read-only work is allowed, including:

- inspecting files, diffs, history, logs, configuration, and dependency metadata;
- searching the repository;
- explaining findings and proposing a plan;
- running commands that do not intentionally modify tracked project files.

Do not create, edit, rename, move, delete, generate, or auto-format project files before approval. This restriction includes production code, tests, build files, configuration, documentation, database migrations, dependencies, lockfiles, and generated sources.

If a supposedly read-only command changes tracked files, stop, disclose the exact changes, and wait for instructions. Do not silently revert or keep them.

## Small-batch policy

Each batch must be independently understandable, reviewable, and verifiable.

- A batch must have one narrowly defined purpose.
- Prefer changing 1-3 files and no more than about 100 changed lines in total (additions plus deletions).
- If a coherent change must exceed either limit, explain why and obtain explicit approval for that larger batch before editing.
- Do not mix feature work, bug fixes, refactoring, formatting, dependency changes, or unrelated cleanup in one batch.
- Avoid repository-wide formatting and broad mechanical rewrites.
- Keep diffs focused: do not reformat or reorder unrelated code.
- Never make multiple planned batches at once, even if they are simple.
- If new required work is discovered during implementation, finish only the safe approved scope when possible, then propose a new batch. If the approved batch cannot be completed safely without expanding scope, stop and ask the user.

## Java style

Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) as the baseline unless an enforced repository formatter, Checkstyle configuration, or clearly established local convention is stricter or conflicts with it. In that case, follow the repository rule and mention the deviation.

In particular:

- use 2-space indentation and never tabs;
- keep braces on the same line as the declaration or control statement;
- use descriptive names and standard Java naming conventions;
- keep imports explicit and organized; do not use wildcard imports;
- use one top-level class per source file;
- keep methods focused and prefer simple control flow;
- avoid unnecessary abbreviations, clever constructs, and deeply nested logic;
- preserve compatibility with the Java version configured by the project;
- add comments only when they explain intent, constraints, or non-obvious decisions; do not narrate obvious code.

Do not introduce or reconfigure a formatter, linter, or style plugin without a separately approved batch.

## Clean code and design discipline

- Make the smallest change that fully satisfies the approved requirement.
- Preserve existing behavior unless a behavior change is explicitly part of the approved batch.
- Follow the repository's existing architecture and patterns before introducing new abstractions.
- Keep responsibilities narrow and dependencies explicit.
- Prefer clear domain language over generic names such as `data`, `info`, `manager`, `helper`, or `utils` when a precise name is available.
- Avoid duplication, but do not create an abstraction until it makes the current code clearer.
- Do not add speculative extensibility, premature optimization, dead code, commented-out code, or unrelated TODOs.
- Handle errors deliberately; do not swallow exceptions or catch overly broad exceptions without a justified boundary.
- Do not change public APIs, persistence schemas, serialization formats, security behavior, or dependencies unless the approved plan explicitly calls this out.
- Update or add focused tests when behavior changes. Do not weaken, delete, or bypass tests merely to make validation pass.

## Validation and handoff

Run the narrowest relevant non-Gradle validation for the approved batch, followed by broader validation only when justified and safe. Do not run Gradle commands unless the user explicitly requests them. Do not routinely mention omitted Gradle validation or recommend Gradle commands in batch handoffs. Report only checks actually performed and material unresolved risks. Do not edit additional files merely to make an unrelated failure pass.

After every batch, report:

- files changed;
- concise description of the changes;
- tests and checks run, with their results;
- any unverified behavior, risks, assumptions, or unrelated failures;
- the proposed next batch, if any.

Then stop and wait for the user's review and explicit approval before making further changes.
