-- Purpose is to listen on a defined port for commands to pass to mpv

local  port = 755;

local  socket = require("socket")
local  conn = socket.udp();
local conn_up = socket.udp();

conn:setsockname("*", port);
conn:settimeout(0);

function send_name()
local title =mp.get_property("media-title");
local length = math.floor(mp.get_property("length"));
local pos=math.floor(mp.get_property("time-pos"));
conn_up:sendto(title.."$"..length.."$"..pos, "192.168.100.168", 756);
end
mp.register_event("file-loaded", send_name);

local commands = {
    {"PAUSE", "set pause yes"},
    {"PLAY", "set pause no"},
    {"TPAUSE", "cycle pause"},
    {"TPLAY", "cycle pause"},
    {"FORWARD", "seek 5"},
    {"BACK", "seek -5"},
    {"STEP", "frame_step"},
    {"STEPBACK", "frame_step_back"},
    {"MUTE", "cycle mute"},
    {"VOLUME UP", "add volume 5"},
    {"VOLUME DOWN", "add volume -5"},
    {"VOLUME MAX", "set volume 100"},
    {"CYCLE A", "cycle aid"},
    {"CYCLE V", "cycle vid"},
    {"CYCLE S", "cycle sid"},
    {"FULLSCREEN", "set fullscreen yes"},
    {"WINDOWED", "set fullscreen no"},
    {"TFULLSCREEN", "cycle fullscreen"},
    {"TWINDOWED", "cycle fullscreen"},
    {"QUIT", "quit"}
}

function do_things(data)
  local arr={}
for s in data.gmatch(data, "%S+") do
 arr[#arr+1]=s;
end
if (arr[1]=="seek") then mp.set_property("time-pos", tonumber(arr[2])); end

    for n, ent in pairs(commands) do
        if (data == ent[1]) then
            mp.command(ent[2]);
            return;
        end

    end
end


function check_socket()
    local data = conn:receive();
    if (data ~= nil) then
		 do_things(data);
    end
end

local timer=mp.add_periodic_timer(0.5, check_socket);
mp.add_periodic_timer(0.5, send_name);

function cleanup(event)
    conn:close();
conn_up:close();
end

mp.register_event("shutdown", cleanup);
