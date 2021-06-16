import { Layout, Menu, Col, Row, Card, Spin, Slider, Avatar, Select, Radio, Input, Button} from 'antd';

import React from "react";
import {
    UploadOutlined,
    PieChartOutlined,
    SortAscendingOutlined,
    FilterOutlined,
    CheckOutlined
} from '@ant-design/icons';
import EditTable from "./EditTable";

//need to fetch data when: sort is clicked, search is entered,

const { Header, Content, Sider } = Layout;
const { Search } = Input;
const { Option } = Select;

class MainPage extends React.Component {
    state = {
        collapsed: false,
        data: [],
        marks: {},
        minSal: 0,
        maxSal: 1000,
        loading: true,
        filterOptions :{
            searchOption: "id",
            searchValue:"",
            sortOption:"id",
            sortOrder:"asc",
            filterValue:[10000,100000]
        }
    };

    onCollapse = collapsed => {
        console.log(collapsed);
        this.setState({ collapsed });
    };

    componentDidMount = () => {
        const httpUrl = `users`;
        fetch(httpUrl)
            .then(response => response.json())
            .then(data => data.map( d => ({...d, key: d.id})))
            .then(data => this.setState({data}))
            .catch((error)  => { console.error(error);})
            .finally(() => {
                this.setState({loading: false});
                this.setState({maxSal: Math.max.apply(Math, this.state.data.map(function(o) { return o.salary; }))});
                this.setState({minSal: Math.min.apply(Math, this.state.data.map(function(o) { return o.salary; }))});

                let marks = {};
                const interval = (this.state.maxSal - this.state.minSal)/5;
                for (let i = 0; i <= 5; i++) {
                    const number = this.state.minSal + (i * interval);
                    marks[this.state.minSal + (i * interval)] = {...
                        {
                        style: {
                            color: '#ffffff',
                        }
                    ,
                        label: <strong>{Math.ceil(number).toString()}</strong>
                    }
                }
                }
                this.setState({marks});
                let filterOptions = {...this.state.filterOptions};
                filterOptions.filterValue = [this.state.minSal, this.state.maxSal];
                this.setState({filterOptions})
            });
    };

    onSliderChange = filterValue => {
        let filterOptions = {...this.state.filterOptions};
        filterOptions.filterValue = filterValue;
        this.setState({filterOptions})
    };

    handleChangeOption = (sortOption) => {
        console.log(`selected ${sortOption}`);
        let filterOptions = {...this.state.filterOptions};
        filterOptions.sortOption = sortOption;
        this.setState({filterOptions})
    };

    handleChangeSearchOption = (searchOption) => {
        console.log(`handle search option ${searchOption}`);
        let filterOptions = {...this.state.filterOptions};
        filterOptions.searchOption = searchOption;
        this.setState({filterOptions})
    };

    onPressEnter = (e) => {
        console.log(`press enter:${e.target.value}`);
        const searchValue = e.target.value;

        console.log('search:', searchValue);
        let filterOptions = {...this.state.filterOptions};
        filterOptions.searchValue = searchValue;
        this.setState({filterOptions}, () => this.fetchFilteredResults());

    };

    handleChangeOrder = (e) => {
        console.log(`handle change order:${e.target.value}`);
        const sortOrder = e.target.value;
        let filterOptions = {...this.state.filterOptions};
        filterOptions.sortOrder = sortOrder;
        this.setState({filterOptions})
    };

    fetchFilteredResults = () => {
        const httpUrl = `users/sortOption=${this.state.filterOptions.sortOption}&sortOrder=${this.state.filterOptions.sortOrder}&filterValue=${Object.values(this.state.filterOptions.filterValue)[0]}-${Object.values(this.state.filterOptions.filterValue)[1]}&searchOption=${this.state.filterOptions.searchOption}&searchValue=${this.state.filterOptions.searchValue}`;

        // const httpUrl = `users/sortOption=${this.state.filterOptions.sortOption}&sortOrder=${this.state.filterOptions.sortOrder}`;
        fetch(httpUrl)
            .then(response => response.json())
            .then(data => this.setState(this.setState({data})));
    };

    onClickSort = (e) => {
        console.log("click sorteddd");
        this.fetchFilteredResults();

    };

    onClickFilter = (e) => {
        console.log("click filtered");
        this.fetchFilteredResults();
    };

    onSearch = (searchValue) => {

        console.log('search:', searchValue);
        let filterOptions = {...this.state.filterOptions};
        filterOptions.searchValue = searchValue;
        this.setState({filterOptions},  () => this.fetchFilteredResults());
    };

    onSearchClear = () => {
        console.log("search clear");
        let filterOptions = {...this.state.filterOptions};
        filterOptions.searchValue = "";
        this.setState({filterOptions},  () =>this.fetchFilteredResults());
    };

    render() {
        const { collapsed } = this.state;

        if(this.state.loading) return (<Spin/>);

        const marks = this.state.marks;

        return (
            <Layout style={{ minHeight: '100vh' }}>
                <Sider collapsible collapsed={collapsed} onCollapse={this.onCollapse}>
                    <div className="logo" />
                    <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                        <Menu.Item key="1" icon={<PieChartOutlined />}>
                            Current Employees
                        </Menu.Item>
                        <Menu.Item key="2" icon={<UploadOutlined />}>
                            Upload CSV
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout className="site-layout">
                    <Header className="site-layout-background" style={{ padding: 0 }} />
                    <Content style={{ margin: '0 16px' }}>
                        <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
                            <div  style={{padding:20}}>
                                <div style ={{marginBottom: 20}}>
                                    <Row gutter={16} type="flex">
                                        <Col span={8} style={{height: "100%"}}>
                                            <Card bordered={false} style={{backgroundColor: "#5b6069"}}>
                                                <div style={{ alignItems:"center", marginTop:"15"}}>
                                                        <Avatar size={58} style={{color: "#000000", backgroundColor: "#ffffff"}} icon={<SortAscendingOutlined/>} />
                                                        <h1 style={{fontSize: 30, color:"#ffffff"}}> Sort By: </h1>
                                                </div>
                                                <div >
                                                    <Select defaultValue="id" style={{flex:30, marginBottom:15, width:200}} onChange={this.handleChangeOption}>
                                                        <Option value="id">Employer Id</Option>
                                                        <Option value="login">Login Id</Option>
                                                        <Option value="name">Name</Option>
                                                        <Option value="salary">Salary</Option>xw
                                                    </Select>
                                                    <Radio.Group onChange={this.handleChangeOrder} defaultValue="asc" buttonStyle="solid">
                                                        <Radio.Button value="asc">Ascending</Radio.Button>
                                                        <Radio.Button value="desc">Descending</Radio.Button>
                                                    </Radio.Group>
                                                    <Button type="primary" icon={<CheckOutlined />} style={{flex:30, marginTop:15, width:200,  backgroundColor: "#339933", borderColor:"#339933"}} onClick={this.onClickSort}>
                                                        Sort
                                                    </Button>
                                                </div>

                                            </Card>
                                        </Col>
                                        <Col span={16}>
                                            <Card bordered={false} style={{backgroundColor: "#5b6069"}}>
                                                <div style={{ alignItems:"center", marginTop:"30"}}>
                                                    <Avatar size={58} style={{color: "#000000", backgroundColor: "#ffffff"}} icon={<FilterOutlined />} />
                                                    <h1 style={{fontSize: 30, color:"#ffffff"}}> Filter By Salary </h1>
                                                </div>
                                                <Slider key={`slider`} range marks={marks} max={this.state.maxSal} min={this.state.minSal} value={this.state.filterOptions.filterValue}  trackStyle={{backgroundColor: "#2563e5"}} handleStyle={{backgroundColor:"#2563e5"}}  onChange={this.onSliderChange} style={{marginTop:30}}/>

                                                <Button type="primary" icon={<CheckOutlined />} style={{flex:30, marginTop:38, width:400,  backgroundColor: "#339933", borderColor:"#339933"}} onClick={this.onClickFilter}>
                                                    Filter
                                                </Button>
                                            </Card>
                                        </Col>
                                    </Row>
                                    </div>
                                <Row>
                                    <Col span={24}>
                                        <Card bordered={false} style={{backgroundColor: "#5b6069"}}>
                                            <div style={{ alignItems:"center", marginTop:"15"}}>
                                                <Avatar size={58} style={{color: "#000000", backgroundColor: "#ffffff"}} icon={<FilterOutlined />} />
                                                <h1 style={{fontSize: 30, color:"#ffffff"}}> Search By: </h1>
                                            </div>
                                            <div style={{ alignItems:"center", marginTop:"15"}}>

                                            <Select defaultValue="id" style={{flex:30, marginRight: 15, width:200}} onChange={this.handleChangeSearchOption}>
                                                <Option value="id">Employer Id</Option>
                                                <Option value="login">Login Id</Option>
                                                <Option value="name">Name</Option>
                                                <Option value="salary">Salary</Option>
                                            </Select>
                                            <Search allowClear placeholder="input search text" onSearch={this.onSearch} style={{ width: 500 }} onPressEnter={this.onPressEnter} onClear={this.onSearchClear} />
                                            </div>
                                        </Card>
                                    </Col>
                                </Row>
                            </div>
                            <EditTable data = {this.state.data}/>
                        </div>
                    </Content>
                </Layout>
            </Layout>
        );
    }
}

export default MainPage;