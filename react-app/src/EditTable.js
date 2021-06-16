import React, { useState, useEffect } from 'react';
import { Table, Input, InputNumber, Popconfirm, Form, Typography, Space } from 'antd';
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
    console.log(data);
    console.log(props.data);

    const editRecord = (record) => {
        form.setFieldsValue({
            id: '',
            login: '',
            name: '',
            ...record,
        });
        setEditingKey(record.key);
    };

    const deleteRecord = (record) => {
        console.log("deleting record"+ record);
    }

    const cancel = () => {
        setEditingKey('');
    };

    const save = async (key) => {
        try {
            const row = await form.validateFields();
            const newData = [...data];
            const index = newData.findIndex((item) => key === item.key);

            if (index > -1) {
                const item = newData[index];
                newData.splice(index, 1, { ...item, ...row });
                setData(newData);
                setEditingKey('');
            } else {
                newData.push(row);
                setData(newData);
                setEditingKey('');
            }
            console.log(newData);
        } catch (errInfo) {
            console.log('Validate Failed:', errInfo);
        }
    };

    const columns = [
        {
            title: 'Employee Id',
            dataIndex: 'id',
            width: '15%',
            editable: true,
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