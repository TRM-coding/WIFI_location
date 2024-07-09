import json

with open('./415s.json', 'r') as f:
    data_list = []
    for line in f:
        json_obj = json.loads(line)
        data_list.append(json_obj)

    # 去重
    data_set = set(map(json.dumps, data_list))
    unique_list = list(map(json.loads, data_set))

# 将去重后的数据写入新的JSON文件
with open('./415s_unique.json', 'w') as f:
    json.dump(unique_list, f, indent=4)
