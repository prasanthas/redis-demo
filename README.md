# redis-demo

## Recodmended Practices
1. Avoid long keys. They take up more memory and require longer lookup times because they have to be compared byte-by-byte.
2. Use keys which can identify the data. For example, “sport:football;date:2008-02-02” would be a better key than "fb:8-2-2".
3. Use a convention. A good one is “object:id”, as in "sport:football"

