# Farming Contract Bank Seed

A RuneLite plugin that adds a Farming contract section to the top of the bank when you still need to plant your active contract. The section shows the banked seed and, for tree contracts, the corresponding banked sapling. The normal bank ordering remains unchanged below it.

The section is hidden while the contract crop is growing, fully grown, or diseased. Empty, dead, occupied-by-another-crop, and unknown patch states show the section.

The plugin uses RuneLite's Time Tracking plugin to identify the active contract and its patch state. It changes only the bank's visual layout; it does not move items between bank slots.

## Development

```sh
./gradlew build
```

For local in-game testing, use the sibling `runelite-plugin-dev-client` harness.

## License

BSD 2-Clause License. See [LICENSE](LICENSE).
