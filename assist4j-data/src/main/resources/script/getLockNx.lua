return redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'NX')
