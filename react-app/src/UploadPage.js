import { Layout, Menu, message, Spin, Upload, Button } from 'antd';
import { withRouter } from "react-router-dom";

import React from "react";
import {
    UploadOutlined,
    PieChartOutlined,
} from '@ant-design/icons';

const { Header, Content, Sider } = Layout;

class UploadPage extends React.Component {
    state = {
        collapsed: false,
        };

    onClickUpload = () => {
        this.props.history.push("/upload");
    }

    onClickEmployee = () => {
        this.props.history.push("/");
    }

    onCollapse = collapsed => {
        console.log(collapsed);
        this.setState({ collapsed });
    };

    componentDidMount = () => {
         console.log("mounted");
    };

    onChange = (info) => {
        if (info.file.status !== 'uploading') {
            console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
           message.success(`${info.file.name} file uploaded successfully`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    render() {
        const { collapsed } = this.state;

        if(this.state.loading) return (<Spin/>);

        return (
            <Layout style={{ minHeight: '100vh' }}>
                <Sider collapsible collapsed={collapsed} onCollapse={this.onCollapse}>
                    <div className="logo" />
                    <Menu theme="dark" defaultSelectedKeys={['2']} mode="inline">
                        <Menu.Item key="1" icon={<PieChartOutlined />} onClick={this.onClickEmployee}>
                            Current Employees
                        </Menu.Item>
                        <Menu.Item key="2" icon={<UploadOutlined />} onClick={this.onClickUpload}>
                            Upload CSV
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout className="site-layout">
                    <Header className="site-layout-background" style={{ padding: 0 }} />
                    <Content style={{ margin: '0 16px' }}>
                        <Upload accept=".csv">
                            <Button icon={<UploadOutlined /> } style={{ margin: 50, width:200}}>Click to Upload</Button>
                        </Upload>,
                    </Content>
                </Layout>
            </Layout>
        );
    }
}

export default withRouter(UploadPage);