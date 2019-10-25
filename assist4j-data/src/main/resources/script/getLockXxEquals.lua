if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'XX')
else
    return 'fail'
end
