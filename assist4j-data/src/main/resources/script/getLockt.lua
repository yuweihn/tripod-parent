local res = redis.call('get', KEYS[1])
if res == ARGV[1] then
    local r = redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'XX')
    if ('OK' == r) then
        return ARGV[1]
    else
        return nil
    end
else
    local r = redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'NX')
    if ('OK' == r) then
        return ARGV[1]
    else
        return nil
    end
end