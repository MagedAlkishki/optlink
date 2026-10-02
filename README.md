# OptLink

A portfolio demo of an opt-in-first B2B consumer data marketplace: consumers
explicitly consent to be listed, companies subscribe to reveal full contact
details, and every reveal is written to a permanent, queryable access log.

**All data in this project is invented.** No real person's information is
collected or processed anywhere in this codebase. That's a deliberate design
choice, not an oversight — see "Why a demo, not a live product" below.

## The idea

The original plan was a real marketplace: people list themselves and get
paid when a business wants to reach them, instead of that value going to
data brokers who scrape or buy their information without asking. Companies
would subscribe for access, and everyone involved — the consumer, the
company, and eventually a regulator — could see exactly who accessed what
and when.

## Why a demo, not a live product

Researching what it would actually take to launch this in Egypt surfaced a
hard blocker: the Personal Data Protection Law (151/2020) and its executive
regulations require a data controller/processor to be licensed by the
Personal Data Protection Center (PDPC) before processing anyone's real
personal data commercially — with real penalties, and enforcement tightening
through 2026. A cross-border marketplace would also raise open questions
under GDPR for any EU-based signups. That's a legitimate regulatory
process to go through for a real business, but not something to route
around for a portfolio project.

So this became a demo instead: the same architecture, the same
consent/access-control model, built and working end to end — just with
every consumer and company seeded as fake data on startup, so there's
nothing real to protect and nothing to license.

## What it demonstrates

- **Consent as a first-class, enforced concept.** Signing up without
  checking the consent box is rejected by Bean Validation before it ever
  reaches application code — there's no code path that creates a listed
  consumer without an explicit, timestamped opt-in.
- **Withdrawal that's actually provable.** Withdrawing consent removes a
  consumer from all future search results immediately, but the row isn't
  deleted — so there's a durable record that consent *was* given and *was*
  withdrawn, which is what a real "right to withdraw" needs to be provable.
- **Nothing revealed for free.** Any company can search and see that a
  match exists (name masked, no contact info). Only a company with an
  active subscription gets the real email/phone — and that gate is
  enforced server-side, not left to the frontend to respect.
- **An access log that can't be skipped.** The instant a subscribed company
  reveals a consumer's real contact details, an `AccessLog` row is written.
  A company can pull its own access history at any time. This is the piece
  most scraped-data platforms (Apollo, ZoomInfo, and similar) don't offer.

## Stack

Java 21 &middot; Spring Boot 3.3 &middot; Spring Data JPA &middot; Bean
Validation &middot; H2 in-memory database &middot; a plain HTML/CSS/JS
frontend served as static files, talking to the REST API via `fetch()`.

No build tooling beyond Maven, no frontend framework, no external services —
clone it and run it.

## Running it

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** in a browser. The database is
in-memory and reseeds with fresh demo data every time the app starts (H2
console, if you want to look directly at the tables, is at
`http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:mem:optlink`, user
`sa`, empty password).

### Try it

- **http://localhost:8080/signup.html** — sign up as a consumer (any fake
  details), or withdraw consent using the ID you're given.
- **http://localhost:8080/portal.html** — the company side. Company ID
  `1` ("Cairo Fit Gyms") is pre-seeded as **subscribed**, so its searches
  reveal full contact details and log every reveal. Company ID `2` ("Delta
  Retail Group") is **not subscribed**, so its searches stay masked —
  compare the two to see the gate in action. You can also sign up a new
  company and toggle its subscription yourself.

### API reference

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/consumers/signup` | Create a consumer (requires `consentGiven: true`) |
| DELETE | `/api/consumers/{id}/consent` | Withdraw consent |
| GET | `/api/consumers/search?companyId=&interestCategory=` | Search, masked or full depending on the company's subscription |
| GET | `/api/consumers/count` | Count of currently active, listed consumers |
| POST | `/api/companies/signup` | Create a company |
| POST | `/api/companies/{id}/subscribe` | Activate a company's subscription |
| POST | `/api/companies/{id}/cancel-subscription` | Deactivate it |
| GET | `/api/companies/{id}/access-log` | That company's full reveal history |

## Project layout

```
src/main/java/com/magedalaa/optlink/
  model/       JPA entities: Consumer, Company, AccessLog
  repository/  Spring Data JPA repositories
  service/     Business logic - this is where the subscription gate and
               access-log write actually happen (ConsumerService.toSearchResult)
  controller/  REST controllers + a global exception handler
  dto/         Request/response records for the API boundary
  config/      DataSeeder - fake demo data loaded once on startup
src/main/resources/
  application.yml
  static/      The frontend (index.html, signup.html, portal.html, css/, js/)
```

## A note on the build in this environment

This project was written and reviewed file-by-file in a sandboxed
environment without access to Maven Central, so `mvn compile` could not be
run there to confirm a clean build. Every class, method signature, and
import was manually cross-checked against where it's used across the
codebase before delivery, but running `mvn spring-boot:run` yourself for
the first real compile is worth doing before relying on this for anything
(like a job application) — flag anything that doesn't build cleanly and
it'll get fixed.
