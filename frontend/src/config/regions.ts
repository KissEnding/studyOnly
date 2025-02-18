export interface Region {
  code: string;
  name: string;
  districts: { code: string; name: string; }[];
}

export const ZHEJIANG_REGIONS: Region[] = [
  {
    code: '330100',
    name: '杭州市',
    districts: [
      { code: '330102', name: '上城区' },
      { code: '330103', name: '下城区' },
      { code: '330104', name: '江干区' },
      { code: '330105', name: '拱墅区' },
      { code: '330106', name: '西湖区' },
      { code: '330108', name: '滨江区' },
      { code: '330109', name: '萧山区' },
      { code: '330110', name: '余杭区' },
      { code: '330111', name: '富阳区' },
      { code: '330112', name: '临安区' },
      { code: '330122', name: '桐庐县' },
      { code: '330127', name: '淳安县' },
      { code: '330182', name: '建德市' }
    ]
  },
  {
    code: '330200',
    name: '宁波市',
    districts: [
      { code: '330203', name: '海曙区' },
      { code: '330205', name: '江北区' },
      { code: '330206', name: '北仑区' },
      { code: '330211', name: '镇海区' },
      { code: '330212', name: '鄞州区' },
      { code: '330213', name: '奉化区' },
      { code: '330225', name: '象山县' },
      { code: '330226', name: '宁海县' },
      { code: '330281', name: '余姚市' },
      { code: '330282', name: '慈溪市' }
    ]
  },
  // ... 其他城市数据
]; 