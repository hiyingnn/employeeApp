import { Table, Button } from 'antd';
import React from 'react';

const columns = [
    {
        title: 'Employee Id',
        dataIndex: 'id',
    },
    {
        title: 'Login Id',
        dataIndex: 'login',
    },
    {
        title: 'Name',
        dataIndex: 'name',
    },
    {
        title: "Salary",
        dataIndex: 'salary',
    },
    {
        title: 'Actions',
        dataIndex: 'actions',
    },
];


class RenderedTable extends React.Component {
    state = {
        selectedRowKeys: [], // Check here to configure the default column
        loading: false,
        data: []
    };

    start = () => {
        this.setState({ loading: true});
        // ajax request after empty completing
        setTimeout(() => {
            this.setState({
                selectedRowKeys: [],
                loading: false,
            });
        }, 1000);
    };

    onSelectChange = selectedRowKeys => {
        console.log('selectedRowKeys changed: ', selectedRowKeys);
        this.setState({ selectedRowKeys });
    };

    render() {
        console.log(this.props.data);
        const { loading, selectedRowKeys } = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };
        const hasSelected = selectedRowKeys.length > 0;
        return (
            <div>
                <div style={{ marginBottom: 16 }}>
                    <Button type="primary" onClick={this.start} disabled={!hasSelected} loading={loading}>
                        Reload
                    </Button>
                    <span style={{ marginLeft: 8 }}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
          </span>
                </div>
                <Table rowSelection={rowSelection} columns={columns} dataSource={this.props.data} />
            </div>
        );
    }
}

export default RenderedTable;
