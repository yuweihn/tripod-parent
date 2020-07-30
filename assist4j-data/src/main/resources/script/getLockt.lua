local res = redis.call('get', KEYS[1])
if res == ARGV[1] then
    local r = redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'XX')
    if ('ok' ~= string.lower(r)) then
        res = nil
    end
else
    local r = redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'NX')
    if ('ok' == string.lower(r)) then
        res = ARGV[1]
    end
end
return res