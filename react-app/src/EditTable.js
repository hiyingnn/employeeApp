import React, { useState, useEffect } from 'react';
import { message, Table, Input, InputNumber, Popconfirm, Form, Typography, Space } from 'antd';
import {
    EditOutlined,
    DeleteOutlined
} from '@ant-design/icons';
const EditableCell = ({
                          editing,
                          dataIndex,
                          title,
                          inputType,
                          record,
                          index,
                          children,
                          ...restProps
                      }) => {
    const inputNode = inputType === 'number' ? <InputNumber /> : <Input />;
    return (
        <td {...restProps}>
            {editing ? (
                <Form.Item
                    name={dataIndex}
                    style={{
                        margin: 0,
                    }}
                    rules={[
                        {
                            required: true,
                            message: `Please Input ${title}!`,
                        },
                    ]}
                >
                    {inputNode}
                </Form.Item>
            ) : (
                children
            )}
        </td>
    );
};

const EditTable= (props) => {
    const [form] = Form.useForm();
    const [data, setData] = useState(props.data);
    const [editingKey, setEditingKey] = useState('');

    useEffect(() => setData(props.data), [props.data]);

    const isEditing = (record) => record.key === editingKey;

    const editRecord = (record) => {
        console.log("edited");
        form.setFieldsValue({
            id: '',
            login: '',
            name: '',
            ...record,
        });
        setEditingKey(record.key);
    };

    const deleteRecord = (record) => {
        console.log("deleting record"+ record.id);
        const newData = [...data];

        const index =newData.findIndex(item => record.id === item.id);
        if (index > -1) {
            newData.splice(index, 1);
            setData(newData);
        }

        const requestOptions = {
            method: 'DELETE',
        };
        console.log(requestOptions);
        fetch(`users/${record.id}`, requestOptions)
            .then(response => response.json())
        };

    const cancel = () => {
        setEditingKey('');
    };

    const save = async (key) => {
        try {
            console.log("key", key);
            const row = await form.validateFields();
            row.id = key;
            const newData = [...data];
            const index = newData.findIndex((item) => key === item.key);

            if (index > -1) {
                const item = newData[index];
                newData.splice(index, 1, { ...item, ...row });
                setEditingKey('');
            } else {
                newData.push(row);
                setEditingKey('');
            }

            const requestOptions = {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(row)
            };
            console.log(requestOptions);

            fetch(`users`, requestOptions)
                .then(response => {
                        console.log(response);
                        if (!response.ok) {
                            message.error(`Employee id: ${row.id} update fail.`);
                        } else {
                            message.success(`Employee id: ${row.id} update success.`);
                            setData(newData);
                        }
                    }
                )

        } catch (errInfo) {
            console.log('Validate Failed:', errInfo);
        }
    };

    const columns = [
        {
            title: 'Employee Id',
            dataIndex: 'id',
            width: '15%',
            editable: false,
        },
        {
            title: 'Login Id',
            dataIndex: 'login',
            width: '15%',
            editable: true,
        },
        {
            title: 'Name',
            dataIndex: 'name',
            width: '15%%',
            editable: true,
        },
        {
            title: 'Salary',
            dataIndex: 'salary',
            width: '20%',
            editable: true,
        },
        {
            title: 'Actions',
            dataIndex: 'actions',
            render: (_, record) => {
                const editable = isEditing(record);
                return editable ? (
                        <span>
                <a
                    href="javascript:;"
                    onClick={() => save(record.key)}
                    style={{
                        marginRight: 8,
                    }}
                >
                  Save
                </a>
                <Popconfirm title="Sure to cancel?" onConfirm={cancel}>
                  <a>Cancel</a>
                </Popconfirm>
              </span>
                    ) : (
                    <Space>
                    <Typography.Link disabled={editingKey !== ''} onClick={() => editRecord(record)}>
                            <EditOutlined/>
                        </Typography.Link>
                    <Typography.Link disabled={editingKey !== ''} onClick={() => deleteRecord(record)}>
                        <DeleteOutlined style={{color: "#DC143C"}}/>
                    </Typography.Link>
                    </Space>
                );
            },
        },
    ];
    const mergedColumns = columns.map((col) => {
        if (!col.editable) {
            return col;
        }

        return {
            ...col,
            onCell: (record) => ({
                record,
                inputType: col.dataIndex === 'salary' ? 'number' : 'text',
                dataIndex: col.dataIndex,
                title: col.title,
                editing: isEditing(record),
            }),
        };
    });
    return (
        <Form form={form} component={false}>
            <Table
                components={{
                    body: {
                        cell: EditableCell,
                    },
                }}
                bordered
                dataSource={data}
                columns={mergedColumns}
                rowClassName="editable-row"
                pagination={{
                    onChange: cancel,
                }}
            />
        </Form>
    );
};

export default EditTable;