# BaaBlah 🐑 under the hood ⚙️

## Main Abstractions

### Immutable content enables cloning
- The "latest view" automatically supports selective cloning ie only specific cloned content will need to be changed, not everything.
- Clones can be made from any version.
- Timelines are based on user's cloned content version and the cloned version of any users that they follow.

### User content is immutable and also writeable
- When a user wants to change a post, it is cloned, the clone is updated and the post is marked as edited
- When a user makes changes to content that has been cloned, cloners can adapt or ignore.
  - We can determine if the adaptation can be automated or not

## Main Technologies

### XTDB
A database for maintaining immutable data

### Clojure
A programming language made for working with immutable data

### Scittle
A Clojure based UI tool for rendering and changing immutable data
