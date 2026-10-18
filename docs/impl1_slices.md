# Slices for Assessment 1

The slices run one at a time, in this order, so each one starts when the one before is merged. The exception is slice
7: its first lab run on Tue 27 Oct needs only the skeleton from slice 1. Slices 1 to 4 meet the brief's Assessment 1
list.

| Slice | What | Requirements | Hours | Starts | Done by |
|---|---|---|---|---|---|
| 0 | Generate the project with gdx-liftoff and commit it unchanged; start the licence register | CR_JAVA17, CR_LICENCES | 4 | Sat 17 Oct | Sun 18 Oct |
| 1 | Skeleton: opens paused with a start prompt, pause key, 5:00 timer, end screen, HUD time and message line; JUnit tests for the clock and pause | FR_START_PAUSED, FR_PAUSE, FR_TIMER, FR_TIME_UP, QR_TIMER_ACCURACY, QR_PAUSE_RESPONSE | 8 | Wed 21 Oct | Fri 23 Oct |
| 2 | The student on a simple map with edges, seen from above | FR_MOVE, FR_MAP_EDGES, CR_2D_TOP_DOWN | 6 | Sat 24 Oct | Sun 25 Oct |
| 3 | Two hidden events at map locations, each with a story message; the events counter | FR_EVENT_HIDDEN, FR_EVENT_EFFECT, FR_EVENT_COUNT, FR_COUNTERS | 8 | Mon 26 Oct | Wed 28 Oct |
| 4 | Two starting items on number keys, one of them working without geese; the items counter. Tag `v0.5` | FR_START_ITEMS, FR_USE_ITEM, FR_COUNTERS, QR_EXTENSIBLE | 8 | Wed 28 Oct | Fri 30 Oct |
| 5 | Geese: calm start, arrival, bread dispenser, fullness meter, fly away | FR_GOOSE_ARRIVAL, FR_FEED_GOOSE, FR_GOOSE_LEAVES, QR_PERFORMANCE, QR_NON_VIOLENT | 12 | Sat 31 Oct | Tue 3 Nov |
| 6 | A sound for each cue; text cues checked for size and contrast. Tag `v0.9` (feature freeze) | FR_CUES, QR_CUES, QR_READABLE | 4 | Wed 4 Nov | Wed 4 Nov |
| 7 | Release: one JAR on Windows, macOS and Linux, run on a lab PC, with the licence texts inside; the client signs off that nothing is violent. Tag `v1.0.0` | CR_JAR, CR_DESKTOP, CR_JAVA17, CR_LICENCES, QR_STARTUP, QR_NON_VIOLENT | 4 | Tue 27 Oct (first lab run) | Wed 11 Nov |

Slice 0 is done. Slices 1 to 7 come to 50 hours. The hours are pessimistic guesses for one person, including tests
and Javadoc. They have not been measured.

If time runs short, cut polish first, then slice 6 (keep the text cues), then slice 5 down to fewer geese and simpler
movement. Never cut slices 1 to 4 or 7.
